package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.Expression;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@ComponentTest
class MergeDSLComponentTest {

    private DSL dsl;
    private Connection connection;
    private PreparedStatement ps;
    private ArgumentCaptor<String> sqlCaptor;

    @BeforeEach
    void setUp() throws SQLException {
        dsl = StandardSqlUtil.dsl();
        connection = mock(Connection.class);
        ps = mock(PreparedStatement.class);
        sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);
    }

    @Test
    void basicMergeUsingTableSource() throws SQLException {
        dsl.mergeInto("products")
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
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
    void mergeWithSubquerySource() throws SQLException {
        SelectStatement subquery = dsl.select("id", "name", "price")
                .from("staging_products")
                .where()
                .column("status")
                .eq("active")
                .getCurrentStatement();

        dsl.mergeInto("products")
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .contains("MERGE INTO")
                .contains("USING (SELECT")
                .contains("ON");
    }

    @Test
    void mergeWithConditionalUpdate() throws SQLException {
        dsl.mergeInto("inventory")
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .contains("WHEN MATCHED AND")
                .contains("THEN UPDATE")
                .contains("WHEN MATCHED AND")
                .contains("THEN DELETE")
                .contains("WHEN NOT MATCHED THEN INSERT");
    }

    @Test
    void mergeWithMultipleActionsShowsComplexScenario() throws SQLException {
        // Scenario: Sync products from a source table
        // - If product exists and source has higher version, update it
        // - If product doesn't exist, insert it
        // - We use predicate on WHEN MATCHED to only update if source version is newer

        dsl.mergeInto("target_products")
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
                .buildPreparedStatement(connection);

        assertThat(sqlCaptor.getValue())
                .contains("MERGE INTO \"target_products\"")
                .contains("USING \"source_products\"")
                .contains("ON \"tgt\".\"sku\" = \"src\".\"sku\"")
                .contains("WHEN MATCHED AND \"src\".\"version\" > \"tgt\".\"version\"")
                .contains("THEN UPDATE SET")
                .contains("WHEN NOT MATCHED THEN INSERT");
    }
}
