package io.github.massimiliano.fluentsql4j.dsl.merge;

import static io.github.massimiliano.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Comparison;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Predicate;
import io.github.massimiliano.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.massimiliano.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.plugin.util.StandardSqlUtil;
import io.github.massimiliano.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MergeBuilderTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void basicMergeWithTableSource() throws SQLException {
        new MergeBuilder(specFactory, "target_table")
                .as("tgt")
                .using("source_table", "src")
                .on("tgt", "id", "src", "id")
                .whenMatched()
                .set("value", "new_value")
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("value", ColumnReference.of("src", "value"))
                .build(sqlCaptureHelper.getConnection());

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
                .on("target", "id", "src", "id")
                .whenMatched()
                .set("name", "updated")
                .build(sqlCaptureHelper.getConnection());

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
                .on("target", "id", "src", "id")
                .whenMatched(condition)
                .set("value", 200)
                .build(sqlCaptureHelper.getConnection());

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
                .on("target", "id", "src", "id")
                .whenMatched()
                .delete()
                .build(sqlCaptureHelper.getConnection());

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
                .on("target", "id", "src", "id")
                .whenMatched()
                .set("updated", true)
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .build(sqlCaptureHelper.getConnection());

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
                        .on("target", "id", "src", "id")
                        .whenMatchedThenUpdate(List.of(UpdateItem.of("val", Literal.of(1))))
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("USING clause must be specified");
    }

    @Test
    void throwsExceptionWhenOnConditionNotSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .whenMatchedThenUpdate(List.of(UpdateItem.of("val", Literal.of(1))))
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("ON condition must be specified");
    }

    @Test
    void throwsExceptionWhenNoActionsSpecified() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target", "id", "source", "id")
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one WHEN clause must be specified");
    }

    @Test
    void throwsExceptionWhenColumnsAndValuesMismatch() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target", "id", "source", "id")
                        .whenNotMatchedThenInsert(
                                List.of(ColumnReference.of("target", "id")), List.of(Literal.of(1), Literal.of(2)))
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Number of columns must match number of values");
    }

    @Test
    void fluentApiWithMultipleSets() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target", "id", "src", "id")
                .whenMatched()
                .set("name", "updated")
                .set("status", "active")
                .set("count", 100)
                .build(sqlCaptureHelper.getConnection());

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
                .on("target", "id", "src", "id")
                .whenNotMatched()
                .set("name", "John")
                .set("age", 30)
                .set("active", true)
                .set("salary", 50000.50)
                .build(sqlCaptureHelper.getConnection());

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
                        .on("target", "id", "source", "id")
                        .whenMatched()
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one SET clause must be specified");
    }

    @Test
    void fluentApiThrowsExceptionWhenInsertWithoutColumns() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target", "id", "source", "id")
                        .whenNotMatched()
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("At least one column must be specified");
    }

    @Test
    void fluentApiThrowsExceptionWhenDeleteWithUpdateItems() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source")
                        .on("target", "id", "source", "id")
                        .whenMatched()
                        .set("name", "test")
                        .delete()
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot use delete() with SET clauses");
    }

    @Test
    void fluentApiWithColumnReferences() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target", "id", "src", "id")
                .whenMatched()
                .set("value", ColumnReference.of("src", "new_value"))
                .build(sqlCaptureHelper.getConnection());

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
                .on("target", "id", "src", "id")
                .whenMatched(condition)
                .delete()
                .build(sqlCaptureHelper.getConnection());

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
                .on("target", "id", "src", "id")
                .whenNotMatched(condition)
                .set("id", ColumnReference.of("src", "id"))
                .set("value", ColumnReference.of("src", "value"))
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN NOT MATCHED AND "src"."value" > ? THEN INSERT ("id", "value") VALUES ("src"."id", "src"."value")\
                        """);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
    }

    @Test
    void fluentApiWithExplicitColumnReferences() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target", "id", "src", "id")
                .whenMatched()
                .set("name", ColumnReference.of("src", "name"))
                .set("email", ColumnReference.of("src", "email"))
                .set("age", ColumnReference.of("src", "age"))
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("name", ColumnReference.of("src", "name"))
                .set("email", ColumnReference.of("src", "email"))
                .build(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).isEqualTo("""
                        MERGE INTO "target" \
                        USING "source" AS src \
                        ON "target"."id" = "src"."id" \
                        WHEN MATCHED THEN UPDATE SET "name" = "src"."name", "email" = "src"."email", "age" = "src"."age" \
                        WHEN NOT MATCHED THEN INSERT ("id", "name", "email") VALUES ("src"."id", "src"."name", "src"."email")\
                        """);
    }

    @Test
    void whenMatchedRejectsDotNotationInColumnName() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source", "src")
                        .on("target", "id", "src", "id")
                        .whenMatched()
                        .set("target.name", "value")
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot notation: 'target.name'");
    }

    @Test
    void whenNotMatchedRejectsDotNotationInColumnName() {
        assertThatThrownBy(() -> new MergeBuilder(specFactory, "target")
                        .using("source", "src")
                        .on("target", "id", "src", "id")
                        .whenNotMatched()
                        .set("target.id", ColumnReference.of("src", "id"))
                        .build(sqlCaptureHelper.getConnection()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Column name must not contain dot notation: 'target.id'");
    }
}
