package lan.tlab.r4j.sql.dsl.merge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MergeBuilderTest {

    private DialectRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.dialectRendererStandardSql2008();
    }

    @Test
    void basicMergeWithTableSource() {
        String sql = new MergeBuilder(renderer, "target_table")
                .as("tgt")
                .using("source_table", "src")
                .on("tgt.id", "src.id")
                .whenMatched()
                .set("value", "new_value")
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("value", ColumnReference.of("src", "value"))
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target_table" AS tgt \
                        USING "source_table" AS src \
                        ON "tgt"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "value" = 'new_value' \
                        WHEN NOT MATCHED THEN INSERT ("id", "value") VALUES ("src"."id", "src"."value")\
                        """);
    }

    @Test
    void mergeWithSubquerySource() {
        SelectStatement subquery = SelectStatement.builder().build();

        String sql = new MergeBuilder(renderer, "target")
                .using(subquery, "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("name", "updated")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING (SELECT * FROM ) AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "name" = 'updated'\
                        """);
    }

    @Test
    void mergeWithConditionalActions() {
        Predicate condition = Comparison.gt(ColumnReference.of("src", "value"), Literal.of(100));

        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(condition)
                .set("value", 200)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED AND "src"."value" > 100 THEN UPDATE SET "value" = 200\
                        """);
    }

    @Test
    void mergeWithDelete() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .delete()
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN DELETE\
                        """);
    }

    @Test
    void mergeWithMultipleActions() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("updated", true)
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "updated" = true \
                        WHEN NOT MATCHED THEN INSERT ("id") VALUES ("src"."id")\
                        """);
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

    @Test
    void fluentApiWithMultipleSets() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("name", "updated")
                .set("status", "active")
                .set("count", 100)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "name" = 'updated', "status" = 'active', "count" = 100\
                        """);
    }

    @Test
    void fluentApiWithMixedTypes() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenNotMatched()
                .set("name", "John")
                .set("age", 30)
                .set("active", true)
                .set("salary", 50000.50)
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN NOT MATCHED THEN INSERT ("name", "age", "active", "salary") VALUES ('John', 30, true, 50000.5)\
                        """);
    }

    @Test
    void fluentApiThrowsExceptionWhenUpdateWithoutSets() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenMatched()
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one SET clause must be specified");
    }

    @Test
    void fluentApiThrowsExceptionWhenInsertWithoutColumns() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenNotMatched()
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one column must be specified");
    }

    @Test
    void fluentApiThrowsExceptionWhenDeleteWithUpdateItems() {
        assertThatThrownBy(() -> new MergeBuilder(renderer, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenMatched()
                        .set("name", "test")
                        .delete()
                        .build())
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot use delete() with SET clauses");
    }

    @Test
    void fluentApiWithColumnReferences() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("target.value", ColumnReference.of("src", "new_value"))
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "target"."value" = "src"."new_value"\
                        """);
    }

    @Test
    void fluentApiDeleteWithCondition() {
        Predicate condition = Comparison.lt(ColumnReference.of("src", "value"), Literal.of(0));

        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(condition)
                .delete()
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED AND "src"."value" < 0 THEN DELETE\
                        """);
    }

    @Test
    void fluentApiInsertWithCondition() {
        Predicate condition = Comparison.gt(ColumnReference.of("src", "value"), Literal.of(100));

        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenNotMatched(condition)
                .set("id", ColumnReference.of("src", "id"))
                .set("value", ColumnReference.of("src", "value"))
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN NOT MATCHED AND "src"."value" > 100 THEN INSERT ("id", "value") VALUES ("src"."id", "src"."value")\
                        """);
    }

    @Test
    void fluentApiWithDotNotationForColumnReferences() {
        String sql = new MergeBuilder(renderer, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("name", "src.name")
                .set("email", "src.email")
                .set("age", "src.age")
                .whenNotMatched()
                .set("id", "src.id")
                .set("name", "src.name")
                .set("email", "src.email")
                .build();

        assertThat(sql)
                .isEqualTo(
                        """
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "name" = "src"."name", "email" = "src"."email", "age" = "src"."age" \
                        WHEN NOT MATCHED THEN INSERT ("id", "name", "email") VALUES ("src"."id", "src"."name", "src"."email")\
                        """);
    }
}
