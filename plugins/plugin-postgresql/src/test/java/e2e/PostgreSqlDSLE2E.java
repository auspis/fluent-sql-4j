package e2e;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.dsl.DSLRegistry;
import io.github.auspis.fluentsql4j.dsl.util.ResultSetUtil;
import io.github.auspis.fluentsql4j.dsl.util.ResultSetUtil.RowMapper;
import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.plugin.builtin.postgre.PostgreSqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.builtin.postgre.dsl.PostgreSqlDSL;
import io.github.massimiliano.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.massimiliano.fluentsql4j.test.util.annotation.E2ETest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
        TestDatabaseUtil.dropOrdersTable(connection);
        TestDatabaseUtil.createOrderTable(connection);

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
        TestDatabaseUtil.truncateOrders(connection);
        TestDatabaseUtil.insertSampleOrders(connection);
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
                .build(connection);

        assertThat(ResultSetUtil.list(ps, nameMapper)).hasSize(1).containsAll(List.of("John Doe"));
    }

    @Test
    void pagination() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .column("users", "email")
                .from("users")
                .orderBy()
                .asc("name")
                .fetch(3)
                .offset(1)
                .build(connection);

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
                .orderBy()
                .desc("age")
                .asc("id")
                .build(connection);

        List<List<String>> results = new ArrayList<>(ResultSetUtil.list(ps, mapper));

        results.sort(Comparator.comparingInt(r -> Integer.parseInt(r.get(1))));

        // Verify complete ranking by age descending
        assertThat(results)
                .containsExactly(
                        List.of("Eve", "1"),
                        List.of("Alice", "2"),
                        List.of("Frank", "3"),
                        List.of("John Doe", "4"),
                        List.of("Charlie", "5"),
                        List.of("Henry", "6"),
                        List.of("Grace", "7"),
                        List.of("Jane Smith", "8"),
                        List.of("Diana", "9"),
                        List.of("Bob", "10"));
    }

    @Test
    void windowFunctionRowNumberPartitionsByActive() throws SQLException {
        RowMapper<List<String>> mapper = r -> List.of(
                r.getString("name"), Boolean.toString(r.getBoolean("active")), String.valueOf(r.getInt("active_rank")));

        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .column("users", "active")
                .rowNumber()
                .partitionBy("users", "active")
                .orderByDesc("users", "age")
                .orderByAsc("users", "id")
                .as("active_rank")
                .from("users")
                .orderBy()
                .desc("active")
                .desc("age")
                .asc("id")
                .build(connection);

        List<List<String>> results = new ArrayList<>(ResultSetUtil.list(ps, mapper));

        results.sort(Comparator.<List<String>, Boolean>comparing(r -> Boolean.parseBoolean(r.get(1)))
                .reversed()
                .thenComparingInt(r -> Integer.parseInt(r.get(2))));

        // Verify ranking within each partition (active/inactive)
        assertThat(results)
                .containsExactly(
                        List.of("Eve", "true", "1"),
                        List.of("Alice", "true", "2"),
                        List.of("Frank", "true", "3"),
                        List.of("John Doe", "true", "4"),
                        List.of("Charlie", "true", "5"),
                        List.of("Henry", "true", "6"),
                        List.of("Jane Smith", "true", "7"),
                        List.of("Grace", "false", "1"),
                        List.of("Diana", "false", "2"),
                        List.of("Bob", "false", "3"));
    }

    @Test
    void innerJoinUsersWithOrders() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("users", "name")
                .sum("orders", "total")
                .as("order_total")
                .from("users")
                .innerJoin("orders")
                .on("users", "id", "orders", "userId")
                .groupBy()
                .column("users", "name")
                .orderBy()
                .asc("name")
                .build(connection);

        RowMapper<List<String>> mapper =
                r -> List.of(r.getString("name"), r.getBigDecimal("order_total").toPlainString());

        assertThat(ResultSetUtil.list(ps, mapper))
                .containsExactly(List.of("Alice", "39.99"), List.of("Charlie", "49.99"), List.of("John Doe", "40.98"));
    }

    @Test
    void stringAggFunctionWithBuilder() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("age")
                .expression(
                        dsl.stringAgg("name").orderBy("name").separator(", ").build())
                .as("names")
                .from("users")
                .groupBy()
                .column("age")
                .orderBy()
                .asc("age")
                .build(connection);

        RowMapper<List<String>> mapper = r -> List.of(r.getString("names"), r.getString("age"));

        assertThat(ResultSetUtil.list(ps, mapper))
                .containsExactly(
                        List.of("Bob", "15"),
                        List.of("Diana, Jane Smith", "25"),
                        List.of("Grace", "28"),
                        List.of("Charlie, Henry, John Doe", "30"),
                        List.of("Alice, Frank", "35"),
                        List.of("Eve", "40"));
    }

    @Test
    void arrayAggFunctionWithBuilder() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("age")
                .expression(dsl.arrayAgg("name").orderBy("name").build())
                .as("names_array")
                .from("users")
                .where()
                .column("age")
                .eq(30)
                .groupBy()
                .column("age")
                .build(connection);

        RowMapper<List<String>> mapper = r -> {
            String[] names = (String[]) r.getArray("names_array").getArray();
            return List.of(names);
        };

        assertThat(ResultSetUtil.list(ps, mapper)).containsExactly(List.of("Charlie", "Henry", "John Doe"));
    }

    @Test
    void coalesceAndNullIfFunctionsWithBuilder() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("name")
                .expression(dsl.coalesce(
                        dsl.nullIf(ColumnReference.of("users", "email"), Literal.of("john@example.com")),
                        Literal.of("no-email@example.com")))
                .as("contact_email")
                .from("users")
                .where()
                .column("id")
                .eq(1)
                .or()
                .column("id")
                .eq(2)
                .orderBy()
                .asc("id")
                .build(connection);

        RowMapper<List<String>> mapper = r -> List.of(r.getString("name"), r.getString("contact_email"));

        assertThat(ResultSetUtil.list(ps, mapper))
                .containsExactly(
                        List.of("John Doe", "no-email@example.com"), List.of("Jane Smith", "jane@example.com"));
    }

    @Test
    void toCharAndDateTruncFunctionsWithBuilder() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("name")
                .expression(dsl.toChar(ColumnReference.of("users", "birthdate"), "YYYY-MM-DD"))
                .as("formatted_date")
                .expression(dsl.dateTrunc("month", ColumnReference.of("users", "birthdate")))
                .as("month_start")
                .from("users")
                .where()
                .column("id")
                .eq(1)
                .build(connection);

        try (var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("formatted_date")).matches("\\d{4}-\\d{2}-\\d{2}");
            assertThat(rs.getDate("month_start")).isNotNull();
        }
    }

    @Test
    void jsonbAggFunctionWithBuilder() throws SQLException {
        PreparedStatement ps = dsl.select()
                .column("age")
                .expression(dsl.jsonbAgg("name").orderBy("name").build())
                .as("names_json")
                .from("users")
                .where()
                .column("age")
                .eq(30)
                .groupBy()
                .column("age")
                .build(connection);

        RowMapper<String> mapper = r -> r.getString("names_json");

        List<String> results = ResultSetUtil.list(ps, mapper);

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).contains("Charlie", "Henry", "John Doe");
    }

    @Test
    void aggregateFunctionsShouldWork() throws SQLException {
        PreparedStatement ps =
                dsl.select().sum("age").as("total_age").from("users").build(connection);

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
                .build(connection);

        RowMapper<Integer> countMapper = r -> r.getInt("distinct_ages");
        List<Integer> counts = ResultSetUtil.list(ps, countMapper);

        assertThat(counts).hasSize(1);
        assertThat(counts.get(0)).isEqualTo(6); // Distinct ages: 15, 25, 28, 30, 35, 40
    }
}
