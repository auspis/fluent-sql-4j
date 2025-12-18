package e2e.system;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.DSLRegistry;
import lan.tlab.r4j.jdsql.dsl.util.ResultSetUtil;
import lan.tlab.r4j.jdsql.dsl.util.ResultSetUtil.RowMapper;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.builtin.postgre.PostgreSqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.builtin.postgre.dsl.PostgreSqlDSL;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.E2ETest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for PostgreSqlDSL with DSLRegistry and real PostgreSQL database.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real PostgreSQL database operations using Testcontainers.
 */
@E2ETest
@Testcontainers
class PostgreSqlDSLE2E {

    @Container
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static DSLRegistry registry;
    private static Connection connection;
    private static RowMapper<String> nameMapper;

    private PostgreSqlDSL dsl;

    @BeforeAll
    static void beforeAll() throws Exception {
        registry = DSLRegistry.createWithServiceLoader();

        connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        TestDatabaseUtil.dropUsersTable(connection);
        TestDatabaseUtil.createUsersTable(connection);

        nameMapper = r -> r.getString("name");
    }

    @AfterAll
    static void afterAll() throws Exception {
        connection.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        dsl = (PostgreSqlDSL)
                registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, PostgreSqlDialectPlugin.DIALECT_VERSION)
                        .orElseThrow();
        TestDatabaseUtil.truncateUsers(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @Test
    void pluginDiscoveryOk() {
        Result<DSL> dslResult =
                registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, PostgreSqlDialectPlugin.DIALECT_VERSION);
        assertThat(dslResult).isInstanceOf(Result.Success.class);
        assertThat(dslResult.orElseThrow()).isNotNull().isInstanceOf(PostgreSqlDSL.class);
    }

    @Test
    void select() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .column("users", "email")
                .from("users")
                .where()
                .column("email")
                .eq("john@example.com")
                .buildPreparedStatement(connection);

        assertThat(ResultSetUtil.list(ps, nameMapper)).hasSize(1).containsAll(List.of("John Doe"));
    }

    @Test
    void pagination() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .column("users", "email")
                .from("users")
                .orderBy("name")
                .fetch(3)
                .offset(1)
                .buildPreparedStatement(connection);

        assertThat(ResultSetUtil.list(ps, nameMapper)).hasSize(3).containsAll(List.of("Bob", "Charlie", "Diana"));
    }

    @Test
    void windowFunctionRowNumberOrdersByAgeDescending() throws SQLException {
        RowMapper<List<String>> mapper = r -> List.of(r.getString("name"), String.valueOf(r.getInt("age_rank")));

        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .column("users", "age")
                .rowNumber()
                .orderByDesc("users", "age")
                .orderByAsc("users", "id")
                .as("age_rank")
                .from("users")
                .orderByDesc("age")
                .orderBy("id")
                .buildPreparedStatement(connection);

        List<List<String>> results = ResultSetUtil.list(ps, mapper);

        // Verify first few rows (Eve is oldest at 40)
        assertThat(results).isNotEmpty();
        assertThat(results.get(0)).containsExactly("Eve", "1");
    }

    @Test
    void stringAggFunction() throws SQLException {
        // Test that STRING_AGG works with DSL
        ScalarExpression stringAggExpr = dsl.stringAgg("name").separator(", ").build();

        // Verify the expression is created correctly
        assertThat(stringAggExpr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) stringAggExpr;
        assertThat(call.functionName()).isEqualTo("STRING_AGG");
    }

    @Test
    void arrayAggFunction() throws SQLException {
        // Test that ARRAY_AGG works with DSL
        ScalarExpression arrayAggExpr = dsl.arrayAgg("name").build();

        // Verify the expression is created correctly
        assertThat(arrayAggExpr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) arrayAggExpr;
        assertThat(call.functionName()).isEqualTo("ARRAY_AGG");
    }

    @Test
    void coalesceFunction() throws SQLException {
        // Test that COALESCE works with DSL
        ScalarExpression coalesceExpr =
                dsl.coalesce(ColumnReference.of("", "email"), Literal.of("no-email@example.com"));

        // Verify the expression is created correctly
        assertThat(coalesceExpr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) coalesceExpr;
        assertThat(call.functionName()).isEqualTo("COALESCE");
    }

    @Test
    void nullIfFunction() throws SQLException {
        // Test that NULLIF works with DSL
        ScalarExpression nullIfExpr = dsl.nullIf(ColumnReference.of("", "email"), Literal.of("john@example.com"));

        // Verify the expression is created correctly
        assertThat(nullIfExpr).isInstanceOf(CustomFunctionCall.class);
        CustomFunctionCall call = (CustomFunctionCall) nullIfExpr;
        assertThat(call.functionName()).isEqualTo("NULLIF");
    }

    @Test
    void aggregateFunctionsShouldWork() throws SQLException {
        PreparedStatement ps =
                dsl.select().sum("age").as("total_age").from("users").buildPreparedStatement(connection);

        RowMapper<Integer> sumMapper = r -> r.getInt("total_age");
        List<Integer> totals = ResultSetUtil.list(ps, sumMapper);

        assertThat(totals).hasSize(1);
        assertThat(totals.get(0)).isEqualTo(293); // Sum of all sample user ages
    }

    @Test
    void countDistinctShouldWork() throws SQLException {
        PreparedStatement ps = dsl.select()
                .countDistinct("age")
                .as("distinct_ages")
                .from("users")
                .buildPreparedStatement(connection);

        RowMapper<Integer> countMapper = r -> r.getInt("distinct_ages");
        List<Integer> counts = ResultSetUtil.list(ps, countMapper);

        assertThat(counts).hasSize(1);
        assertThat(counts.get(0)).isEqualTo(6); // Distinct ages: 15, 25, 28, 30, 35, 40
    }
}
