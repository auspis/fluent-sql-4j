package lan.tlab.r4j.sql.e2e.merge;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MergeE2ETest {

    private DSL dsl;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
    }

    @Test
    void basicMergeUsingTableSource() {
        String sql = dsl.mergeInto("products")
                .as("p")
                .using("new_products", "np")
                .on("p.product_id", "np.product_id")
                .whenMatchedThenUpdate(List.of(
                        UpdateItem.of("product_name", ColumnReference.of("np", "product_name")),
                        UpdateItem.of("price", ColumnReference.of("np", "price"))))
                .whenNotMatchedThenInsert(
                        List.of(
                                ColumnReference.of("p", "product_id"),
                                ColumnReference.of("p", "product_name"),
                                ColumnReference.of("p", "price")),
                        List.of(
                                (Expression) ColumnReference.of("np", "product_id"),
                                (Expression) ColumnReference.of("np", "product_name"),
                                (Expression) ColumnReference.of("np", "price")))
                .build();

        assertThat(sql)
                .contains("MERGE INTO")
                .contains("products")
                .contains("USING")
                .contains("new_products")
                .contains("ON")
                .contains("product_id")
                .contains("WHEN MATCHED THEN UPDATE")
                .contains("SET")
                .contains("WHEN NOT MATCHED THEN INSERT")
                .contains("VALUES");
    }

    @Test
    void mergeWithSubquerySource() {
        SelectStatement subquery = dsl.select("id", "name", "price")
                .from("staging_products")
                .where("status")
                .eq("active")
                .getCurrentStatement();

        String sql = dsl.mergeInto("products")
                .as("p")
                .using(subquery, "src")
                .on("p.id", "src.id")
                .whenMatchedThenUpdate(List.of(
                        UpdateItem.of("name", ColumnReference.of("src", "name")),
                        UpdateItem.of("price", ColumnReference.of("src", "price"))))
                .whenNotMatchedThenInsert(
                        List.of(
                                ColumnReference.of("p", "id"),
                                ColumnReference.of("p", "name"),
                                ColumnReference.of("p", "price")),
                        List.of(
                                (Expression) ColumnReference.of("src", "id"),
                                (Expression) ColumnReference.of("src", "name"),
                                (Expression) ColumnReference.of("src", "price")))
                .build();

        assertThat(sql).contains("MERGE INTO").contains("USING (SELECT").contains("ON");
    }

    @Test
    void mergeWithConditionalUpdate() {
        String sql = dsl.mergeInto("inventory")
                .as("i")
                .using("new_stock", "ns")
                .on("i.product_id", "ns.product_id")
                .whenMatchedThenUpdate(
                        Comparison.gt(ColumnReference.of("ns", "quantity"), Literal.of(0)),
                        List.of(UpdateItem.of("quantity", ColumnReference.of("ns", "quantity"))))
                .whenMatchedThenDelete(Comparison.eq(ColumnReference.of("ns", "quantity"), Literal.of(0)))
                .whenNotMatchedThenInsert(
                        List.of(ColumnReference.of("i", "product_id"), ColumnReference.of("i", "quantity")),
                        List.of((Expression) ColumnReference.of("ns", "product_id"), (Expression)
                                ColumnReference.of("ns", "quantity")))
                .build();

        assertThat(sql)
                .contains("WHEN MATCHED AND")
                .contains("THEN UPDATE")
                .contains("WHEN MATCHED AND")
                .contains("THEN DELETE")
                .contains("WHEN NOT MATCHED THEN INSERT");
    }

    @Test
    void mergeWithMultipleActionsShowsComplexScenario() {
        // Scenario: Sync products from a source table
        // - If product exists and source has higher version, update it
        // - If product doesn't exist, insert it
        // - We use predicate on WHEN MATCHED to only update if source version is newer

        String sql = dsl.mergeInto("target_products")
                .as("tgt")
                .using("source_products", "src")
                .on("tgt.sku", "src.sku")
                .whenMatchedThenUpdate(
                        Comparison.gt(ColumnReference.of("src", "version"), ColumnReference.of("tgt", "version")),
                        List.of(
                                UpdateItem.of("name", ColumnReference.of("src", "name")),
                                UpdateItem.of("price", ColumnReference.of("src", "price")),
                                UpdateItem.of("version", ColumnReference.of("src", "version"))))
                .whenNotMatchedThenInsert(
                        List.of(
                                ColumnReference.of("tgt", "sku"),
                                ColumnReference.of("tgt", "name"),
                                ColumnReference.of("tgt", "price"),
                                ColumnReference.of("tgt", "version")),
                        List.of(
                                (Expression) ColumnReference.of("src", "sku"),
                                (Expression) ColumnReference.of("src", "name"),
                                (Expression) ColumnReference.of("src", "price"),
                                (Expression) ColumnReference.of("src", "version")))
                .build();

        assertThat(sql)
                .contains("MERGE INTO \"target_products\"")
                .contains("USING \"source_products\"")
                .contains("ON \"tgt\".\"sku\" = \"src\".\"sku\"")
                .contains("WHEN MATCHED AND \"src\".\"version\" > \"tgt\".\"version\"")
                .contains("THEN UPDATE SET")
                .contains("WHEN NOT MATCHED THEN INSERT");
    }
}
