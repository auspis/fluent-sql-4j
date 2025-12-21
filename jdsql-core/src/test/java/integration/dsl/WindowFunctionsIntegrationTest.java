package integration.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.window.OverClause;
import lan.tlab.r4j.jdsql.ast.core.expression.window.WindowFunction;
import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.select.SelectBuilder;
import lan.tlab.r4j.jdsql.plugin.util.StandardSqlUtil;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.IntegrationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@IntegrationTest
class WindowFunctionsIntegrationTest {

    private Connection connection;
    private PreparedStatementSpecFactory specFactory;

    @BeforeEach
    void setUp() throws SQLException {
        connection = TestDatabaseUtil.createH2Connection();
        specFactory = StandardSqlUtil.preparedStatementSpecFactory();
        prepareData();
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void rowNumberOrdersBySalary() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rowNumber(OverClause.builder()
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "row_num"))
                .build();

        PreparedStatement ps =
                new SelectBuilder(specFactory, select).from("employees").buildPreparedStatement(connection);

        List<List<Object>> rows = lan.tlab.r4j.jdsql.dsl.util.ResultSetUtil.list(
                ps, rs -> List.of(rs.getString("name"), rs.getInt("row_num")));

        assertThat(rows)
                .containsExactly(List.of("Alice", 1), List.of("Bob", 2), List.of("Carol", 3), List.of("Dave", 4));
    }

    @Test
    void rowNumberPartitionByDepartment() throws SQLException {
        Select select = Select.builder()
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "department")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "name")))
                .projection(new ScalarExpressionProjection(ColumnReference.of("employees", "salary")))
                .projection(new ScalarExpressionProjection(
                        WindowFunction.rowNumber(OverClause.builder()
                                .partitionBy(ColumnReference.of("employees", "department"))
                                .orderBy(Sorting.desc(ColumnReference.of("employees", "salary")))
                                .build()),
                        "dept_row_num"))
                .build();

        PreparedStatement ps =
                new SelectBuilder(specFactory, select).from("employees").buildPreparedStatement(connection);

        List<List<Object>> rows = lan.tlab.r4j.jdsql.dsl.util.ResultSetUtil.list(
                ps, rs -> List.of(rs.getString("department"), rs.getString("name"), rs.getInt("dept_row_num")));

        assertThat(rows)
                .containsExactly(
                        List.of("Engineering", "Alice", 1),
                        List.of("Engineering", "Bob", 2),
                        List.of("HR", "Carol", 1),
                        List.of("HR", "Dave", 2));
    }

    private void prepareData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("DROP TABLE IF EXISTS employees");
            stmt.executeUpdate(
                    "CREATE TABLE employees (id INT PRIMARY KEY, name VARCHAR(100), department VARCHAR(50), salary INT)");
            stmt.executeUpdate("INSERT INTO employees (id, name, department, salary) VALUES "
                    + "(1, 'Alice', 'Engineering', 90000), "
                    + "(2, 'Bob', 'Engineering', 80000), "
                    + "(3, 'Carol', 'HR', 70000), "
                    + "(4, 'Dave', 'HR', 60000)");
        }
    }
}
