package lan.tlab.r4j.jdsql.dsl.merge;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MergeBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void basicMergeWithTableSource() throws SQLException {
        new MergeBuilder(specFactory, "target_table")
                .as("tgt")
                .using("source_table", "src")
                .on("tgt.id", "src.id")
                .whenMatched()
                .set("value", "new_value")
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("value", ColumnReference.of("src", "value"))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                MERGE INTO "target_table" AS tgt \
                USING "source_table" AS src \
                ON "tgt"."id" = "src"."id" \
                WHEN MATCHED THEN UPDATE SET "value" = ? \
                WHEN NOT MATCHED THEN INSERT ("id", "value") VALUES ("src"."id", "src"."value")\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "new_value");
    }

    @Test
    void mergeWithSubquerySource() throws SQLException {
        SelectStatement subquery = SelectStatement.builder().build();

        new MergeBuilder(specFactory, "target")
                .using(subquery, "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("name", "updated")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                MERGE INTO "target" \
                USING (SELECT * FROM ) "src" \
                ON "target"."id" = "src"."id" \
                WHEN MATCHED THEN UPDATE SET "name" = ?\
                """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "updated");
    }

    @Test
    void mergeWithConditionalActions() throws SQLException {
        Predicate condition = Comparison.gt(ColumnReference.of("src", "value"), Literal.of(100));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(condition)
                .set("value", 200)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED AND "src"."value" > ? THEN UPDATE SET "value" = ?\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 200);
    }

    @Test
    void mergeWithDelete() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .delete()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN DELETE\
                        """);
    }

    @Test
    void mergeWithMultipleActions() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("updated", true)
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "updated" = ? \
                        WHEN NOT MATCHED THEN INSERT ("id") VALUES ("src"."id")\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
    }

    @Test
    void throwsExceptionWhenTargetTableIsNull() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target table name cannot be null or empty");
    }

    @Test
    void throwsExceptionWhenTargetTableIsEmpty() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target table name cannot be null or empty");
    }

    @Test
    void throwsExceptionWhenUsingNotSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .on("target.id", "src.id")
                        .whenMatchedThenUpdate(List.of(UpdateItem.of("val", Literal.of(1))))
                        .buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("USING clause must be specified");
    }

    @Test
    void throwsExceptionWhenOnConditionNotSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .whenMatchedThenUpdate(List.of(UpdateItem.of("val", Literal.of(1))))
                        .buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ON condition must be specified");
    }

    @Test
    void throwsExceptionWhenNoActionsSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one WHEN clause must be specified");
    }

    @Test
    void throwsExceptionWhenColumnsAndValuesMismatch() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenNotMatchedThenInsert(
                                List.of(ColumnReference.of("target", "id")), List.of(Literal.of(1), Literal.of(2)))
                        .buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Number of columns must match number of values");
    }

    @Test
    void fluentApiWithMultipleSets() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("name", "updated")
                .set("status", "active")
                .set("count", 100)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "name" = ?, "status" = ?, "count" = ?\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "updated");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 100);
    }

    @Test
    void fluentApiWithMixedTypes() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenNotMatched()
                .set("name", "John")
                .set("age", 30)
                .set("active", true)
                .set("salary", 50000.50)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN NOT MATCHED THEN INSERT ("name", "age", "active", "salary") VALUES (?, ?, ?, ?)\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "John");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 30);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, true);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, 50000.50);
    }

    @Test
    void fluentApiThrowsExceptionWhenUpdateWithoutSets() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenMatched()
                        .buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one SET clause must be specified");
    }

    @Test
    void fluentApiThrowsExceptionWhenInsertWithoutColumns() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenNotMatched()
                        .buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one column must be specified");
    }

    @Test
    void fluentApiThrowsExceptionWhenDeleteWithUpdateItems() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target.id", "source.id")
                        .whenMatched()
                        .set("name", "test")
                        .delete()
                        .buildPreparedStatement(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot use delete() with SET clauses");
    }

    @Test
    void fluentApiWithColumnReferences() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("target.value", ColumnReference.of("src", "new_value"))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "value" = "src"."new_value"\
                        """);
    }

    @Test
    void fluentApiDeleteWithCondition() throws SQLException {
        Predicate condition = Comparison.lt(ColumnReference.of("src", "value"), Literal.of(0));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(condition)
                .delete()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED AND "src"."value" < ? THEN DELETE\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 0);
    }

    @Test
    void fluentApiInsertWithCondition() throws SQLException {
        Predicate condition = Comparison.gt(ColumnReference.of("src", "value"), Literal.of(100));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenNotMatched(condition)
                .set("id", ColumnReference.of("src", "id"))
                .set("value", ColumnReference.of("src", "value"))
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN NOT MATCHED AND "src"."value" > ? THEN INSERT ("id", "value") VALUES ("src"."id", "src"."value")\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
    }

    @Test
    void fluentApiWithDotNotationForColumnReferences() throws SQLException {
        new MergeBuilder(specFactory, "target")
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
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "name" = "src"."name", "email" = "src"."email", "age" = "src"."age" \
                        WHEN NOT MATCHED THEN INSERT ("id", "name", "email") VALUES ("src"."id", "src"."name", "src"."email")\
                        """);
    }
}
