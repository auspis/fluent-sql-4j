package lan.tlab.r4j.sql.dsl.merge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.predicate.Predicate;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MergeBuilderTest {

    private DSL dsl;
    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        dsl = TestDialectRendererFactory.dslStandardSql2008();
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void basicMergeWithTableSource() {
        String sql = new MergeBuilder(renderer, "target_table")
                .as("tgt")
                .using("source_table", "src")
                .on("tgt.id", "src.id")
                .whenMatchedThenUpdate(List.of(UpdateItem.of("value", Literal.of("new_value"))))
                .whenNotMatchedThenInsert(
                        List.of(ColumnReference.of("tgt", "id"), ColumnReference.of("tgt", "value")),
                        List.of((Expression) ColumnReference.of("src", "id"), (Expression)
                                ColumnReference.of("src", "value")))
                .build();

        assertThat(sql)
                .contains("MERGE INTO")
                .contains("target_table")
                .contains("AS")
                .contains("tgt")
                .contains("USING")
                .contains("source_table")
                .contains("src")
                .contains("ON")
                .contains("WHEN MATCHED THEN UPDATE")
                .contains("WHEN NOT MATCHED THEN INSERT");
    }

    @Test
    void mergeWithSubquerySource() {
        SelectStatement subquery = SelectStatement.builder().build();

        String sql = new MergeBuilder(renderer, "target")
                .using(subquery, "src")
                .on("target.id", "src.id")
                .whenMatchedThenUpdate(List.of(UpdateItem.of("name", Literal.of("updated"))))
                .build();

        assertThat(sql).contains("MERGE INTO").contains("USING").contains("ON");
    }

    @Test
    void mergeWithConditionalActions() {
        Predicate condition = Comparison.gt(ColumnReference.of("src", "value"), Literal.of(100));

        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatchedThenUpdate(condition, List.of(UpdateItem.of("value", Literal.of(200))))
                .build();

        assertThat(sql).contains("WHEN MATCHED AND").contains("THEN UPDATE");
    }

    @Test
    void mergeWithDelete() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatchedThenDelete()
                .build();

        assertThat(sql).contains("WHEN MATCHED THEN DELETE");
    }

    @Test
    void mergeWithMultipleActions() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatchedThenUpdate(List.of(UpdateItem.of("updated", Literal.of(true))))
                .whenNotMatchedThenInsert(List.of(ColumnReference.of("target", "id")), List.of((Expression)
                        ColumnReference.of("src", "id")))
                .build();

        assertThat(sql).contains("WHEN MATCHED").contains("WHEN NOT MATCHED");
    }

    @Test
    void throwsExceptionWhenTargetTableIsNull() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target table name cannot be null or empty");
    }

    @Test
    void throwsExceptionWhenTargetTableIsEmpty() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target table name cannot be null or empty");
    }

    @Test
    void throwsExceptionWhenUsingNotSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, "target")
                        .on("target.id", "src.id")
                        .whenMatchedThenUpdate(List.of(UpdateItem.of("val", Literal.of(1))))
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("USING clause must be specified");
    }

    @Test
    void throwsExceptionWhenOnConditionNotSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, "target")
                        .using("source")
                        .whenMatchedThenUpdate(List.of(UpdateItem.of("val", Literal.of(1))))
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ON condition must be specified");
    }

    @Test
    void throwsExceptionWhenNoActionsSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one WHEN clause must be specified");
    }

    @Test
    void throwsExceptionWhenColumnsAndValuesMismatch() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenNotMatchedThenInsert(
                                List.of(ColumnReference.of("target", "id")), List.of(Literal.of(1), Literal.of(2)))
                        .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Number of columns must match number of values");
    }
}
