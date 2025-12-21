package e2e.system;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
                .build(connection);

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
                .orderByDesc("age")
                .orderBy("id")
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
                .orderByDesc("active")
                .orderByDesc("age")
                .orderBy("id")
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
    void stringAggFunctionExecutesOnRealDatabase() throws SQLException {
        // Execute STRING_AGG on real database and verify results
        RowMapper<List<String>> mapper = r -> List.of(r.getString("names"), r.getString("age"));

        // Use raw SQL to execute STRING_AGG since PostgreSQL DSL doesn't have fluent API integration
        PreparedStatement ps = connection.prepareStatement(
                "SELECT age, STRING_AGG(name, ', ' ORDER BY name) as names FROM users GROUP BY age ORDER BY age");

        List<List<String>> results = ResultSetUtil.list(ps, mapper);

        // Verify results contain aggregated names
        assertThat(results).isNotEmpty();
        // Verify at least one age group has multiple concatenated names
        assertThat(results.stream().anyMatch(r -> r.get(0).contains(","))).isTrue();

        // Verify specific age groups
        String age30Names = results.stream()
                .filter(r -> r.get(1).equals("30"))
                .map(r -> r.get(0))
                .findFirst()
                .orElse("");
        assertThat(age30Names).contains("Charlie", "Henry", "John Doe");
    }

    @Test
    void arrayAggFunctionExecutesOnRealDatabase() throws SQLException {
        // Execute ARRAY_AGG on real database and verify results
        PreparedStatement ps = connection.prepareStatement(
                "SELECT age, ARRAY_AGG(name ORDER BY name) as names_array FROM users WHERE age = 30 GROUP BY age");

        try (var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("age")).isEqualTo(30);

            // PostgreSQL returns Array type which can be converted to Java array
            java.sql.Array array = rs.getArray("names_array");
            assertThat(array).isNotNull();

            String[] names = (String[]) array.getArray();
            assertThat(names).contains("Charlie", "Henry", "John Doe");
        }
    }

    @Test
    void coalesceFunctionExecutesOnRealDatabase() throws SQLException {
        // Insert a test row with NULL email
        try (var stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO users (id, name, email, age, active, birthdate, \"createdAt\") "
                    + "VALUES (99, 'Test User', NULL, 30, true, '1990-01-01', '2023-01-01 00:00:00')");
        }

        // Execute COALESCE to replace NULL email with default value
        PreparedStatement ps = connection.prepareStatement(
                "SELECT name, COALESCE(email, 'no-email@example.com') as contact_email FROM users WHERE id = 99");

        try (var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Test User");
            assertThat(rs.getString("contact_email")).isEqualTo("no-email@example.com");
        }

        // Clean up test data
        try (var stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM users WHERE id = 99");
        }
    }

    @Test
    void nullIfFunctionExecutesOnRealDatabase() throws SQLException {
        // Execute NULLIF to convert matching email to NULL
        PreparedStatement ps =
                connection.prepareStatement("SELECT name, NULLIF(email, 'john@example.com') as modified_email "
                        + "FROM users WHERE id IN (1, 2) ORDER BY id");

        try (var rs = ps.executeQuery()) {
            // First user (John Doe) should have NULL email (since it matches the literal)
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("modified_email")).isNull();

            // Second user should have their original email
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("Jane Smith");
            assertThat(rs.getString("modified_email")).isEqualTo("jane@example.com");
        }
    }

    @Test
    void toCharFunctionFormatsTimestamps() throws SQLException {
        // Test TO_CHAR for date/time formatting
        PreparedStatement ps = connection.prepareStatement(
                "SELECT name, TO_CHAR(birthdate, 'YYYY-MM-DD') as formatted_date " + "FROM users WHERE id = 1");

        try (var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            String formattedDate = rs.getString("formatted_date");
            assertThat(formattedDate).matches("\\d{4}-\\d{2}-\\d{2}");
        }
    }

    @Test
    void dateTruncFunctionTruncatesTimestamps() throws SQLException {
        // Test DATE_TRUNC for date truncation
        PreparedStatement ps = connection.prepareStatement(
                "SELECT name, DATE_TRUNC('month', birthdate) as month_start " + "FROM users WHERE id = 1");

        try (var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getDate("month_start")).isNotNull();
        }
    }

    @Test
    void jsonbAggFunctionAggregatesJsonData() throws SQLException {
        // Test JSONB_AGG aggregation
        PreparedStatement ps = connection.prepareStatement(
                "SELECT age, JSONB_AGG(name ORDER BY name) as names_json " + "FROM users WHERE age = 30 GROUP BY age");

        try (var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("age")).isEqualTo(30);

            String jsonResult = rs.getString("names_json");
            assertThat(jsonResult).isNotNull();
            // Should be a JSON array with names
            assertThat(jsonResult).contains("Charlie", "Henry", "John Doe");
        }
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
