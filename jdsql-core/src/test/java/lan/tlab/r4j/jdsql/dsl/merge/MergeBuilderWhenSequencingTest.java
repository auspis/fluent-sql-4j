package lan.tlab.r4j.jdsql.dsl.merge;

import static lan.tlab.r4j.jdsql.test.SqlAssert.assertThatSql;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import lan.tlab.r4j.jdsql.test.helper.SqlCaptureHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MERGE statement WHEN clause sequencing, parameter binding order, and exclusivity tests.
 * Ensures:
 * - WHEN MATCHED and WHEN NOT MATCHED clauses are generated in correct order
 * - Parameters are bound in left-to-right, top-to-bottom order across all WHEN clauses
 * - Multiple conditions on same WHEN type are mutually exclusive
 * - WHEN MATCHED UPDATE/DELETE are alternatives within same WHEN block
 */
class MergeBuilderWhenSequencingTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlCaptureHelper sqlCaptureHelper;

    @BeforeEach
    void setUp() throws SQLException {
        specFactory = StandardSqlRendererFactory.dialectRendererStandardSql();
        sqlCaptureHelper = new SqlCaptureHelper();
    }

    @Test
    void whenMatchedUpdateThenWhenNotMatchedInsert() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("value", 100)
                .whenNotMatched()
                .set("id", 1)
                .set("value", 50)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // WHEN MATCHED must come before WHEN NOT MATCHED
        assertThatSql(sqlCaptureHelper)
                .contains("WHEN MATCHED THEN UPDATE")
                .containsInOrder("WHEN MATCHED", "WHEN NOT MATCHED");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 50);
    }

    @Test
    void whenMatchedDeleteThenWhenNotMatchedInsert() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .delete()
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("data", "new")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHEN MATCHED THEN DELETE")
                .contains("WHEN NOT MATCHED THEN INSERT")
                .containsInOrder("WHEN MATCHED", "WHEN NOT MATCHED");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "new");
    }

    @Test
    void parameterOrderWithConditions() throws SQLException {
        Predicate matchedCondition = Comparison.gt(ColumnReference.of("src", "amount"), Literal.of(1000));
        Predicate notMatchedCondition = Comparison.eq(ColumnReference.of("src", "status"), Literal.of("active"));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(matchedCondition)
                .set("updated", true)
                .whenNotMatched(notMatchedCondition)
                .set("id", ColumnReference.of("src", "id"))
                .set("status", "pending")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // Parameters should be: 1000 (matched condition), true (matched update),
        // "active" (not matched condition), "pending" (not matched insert)
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 1000);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, true);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "pending");
    }

    @Test
    void multipleWhenMatchedWithDifferentConditions() throws SQLException {
        Predicate condition1 = Comparison.gt(ColumnReference.of("src", "value"), Literal.of(100));
        Predicate condition2 = Comparison.lt(ColumnReference.of("src", "value"), Literal.of(50));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(condition1)
                .set("status", "high")
                .whenMatched(condition2)
                .set("status", "low")
                .whenNotMatched()
                .set("id", 1)
                .set("status", "normal")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // Both WHEN MATCHED clauses should appear before WHEN NOT MATCHED
        assertThatSql(sqlCaptureHelper)
                .containsInOrder("WHEN MATCHED AND \"src\".\"value\" > ?", "WHEN MATCHED AND \"src\".\"value\" < ?");
        // Parameters: 100 (first condition), "high" (first update),
        // 50 (second condition), "low" (second update), 1 and "normal" (not matched)
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "high");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 50);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "low");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(6, "normal");
    }

    @Test
    void whenMatchedUpdateFollowedByDelete() throws SQLException {
        // First WHEN MATCHED UPDATE, then separate WHEN MATCHED DELETE
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("updated", true)
                .whenMatched(Comparison.eq(ColumnReference.of("src", "archive"), Literal.of(1)))
                .delete()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper).containsInOrder("WHEN MATCHED THEN UPDATE", "WHEN MATCHED AND", "THEN DELETE");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, true);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 1);
    }

    @Test
    void notMatchedInsertWithMultipleColumns() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched()
                .set("last_updated", "2025-12-21")
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("name", ColumnReference.of("src", "name"))
                .set("email", ColumnReference.of("src", "email"))
                .set("status", "new")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .contains("WHEN NOT MATCHED THEN INSERT (\"id\", \"name\", \"email\", \"status\")")
                .contains("VALUES (\"src\".\"id\", \"src\".\"name\", \"src\".\"email\", ?)");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "2025-12-21");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "new");
    }

    @Test
    void complexSequenceUpdateUpdateInsert() throws SQLException {
        Predicate condition1 = Comparison.eq(ColumnReference.of("src", "type"), Literal.of("A"));
        Predicate condition2 = Comparison.eq(ColumnReference.of("src", "type"), Literal.of("B"));
        Predicate insertCondition = Comparison.ne(ColumnReference.of("src", "priority"), Literal.of(0));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(condition1)
                .set("action", "update_a")
                .whenMatched(condition2)
                .set("action", "update_b")
                .whenNotMatched(insertCondition)
                .set("id", 999)
                .set("action", "insert_new")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .containsInOrder("\"type\" = ?", "\"type\" = ?", "\"priority\" <> ?")
                .containsInOrder("UPDATE SET", "UPDATE SET", "INSERT");
        // Parameters: "A", "update_a", "B", "update_b", 0, 999, "insert_new"
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "A");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "update_a");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "B");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "update_b");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, 0);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(6, 999);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(7, "insert_new");
    }

    @Test
    void whenNotMatchedCanHaveMultipleConditions() throws SQLException {
        Predicate condition1 = Comparison.gt(ColumnReference.of("src", "salary"), Literal.of(50000));
        Predicate condition2 = Comparison.eq(ColumnReference.of("src", "dept"), Literal.of("engineering"));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.emp_id", "src.emp_id")
                .whenNotMatched(condition1)
                .set("emp_id", ColumnReference.of("src", "emp_id"))
                .set("salary_tier", "high")
                .whenNotMatched(condition2)
                .set("emp_id", ColumnReference.of("src", "emp_id"))
                .set("dept_name", "eng")
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .containsInOrder("WHEN NOT MATCHED AND", "WHEN NOT MATCHED AND")
                .contains("INSERT");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 50000);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, "high");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, "engineering");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "eng");
    }

    @Test
    void multipleWhenMatchedWithDeleteTransition() throws SQLException {
        new MergeBuilder(specFactory, "target")
                .using("source")
                .on("target.id", "source.id")
                .whenMatched()
                .set("status", "active")
                .whenMatched(Comparison.eq(ColumnReference.of("source", "type"), Literal.of("ARCHIVE")))
                .delete()
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        assertThatSql(sqlCaptureHelper)
                .containsInOrder("WHEN MATCHED THEN UPDATE SET", "WHEN MATCHED AND", "THEN DELETE");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, "active");
    }

    @Test
    void parameterBindingOrderComplexCase() throws SQLException {
        Predicate matched1 = Comparison.gt(ColumnReference.of("src", "qty"), Literal.of(100));
        Predicate notMatched1 = Comparison.eq(ColumnReference.of("src", "status"), Literal.of("active"));

        new MergeBuilder(specFactory, "target")
                .using("source", "src")
                .on("target.id", "src.id")
                .whenMatched(matched1)
                .set("quantity", 500)
                .set("price", 99.99)
                .whenNotMatched(notMatched1)
                .set("id", ColumnReference.of("src", "id"))
                .set("qty", 1)
                .set("price", 10.50)
                .buildPreparedStatement(sqlCaptureHelper.getConnection());

        // Parameter binding order: matched condition (100), matched sets (500, 99.99),
        // not matched condition ("active"), not matched sets (1, 10.50)
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(1, 100);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(2, 500);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(3, 99.99);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(4, "active");
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(5, 1);
        verify(sqlCaptureHelper.getPreparedStatement()).setObject(6, 10.50);
    }
}
