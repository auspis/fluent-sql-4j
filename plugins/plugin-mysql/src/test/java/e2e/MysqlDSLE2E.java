package e2e;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.dsl.DSLRegistry;
import io.github.auspis.fluentsql4j.dsl.select.OrderByBuilder;
import io.github.auspis.fluentsql4j.dsl.util.ResultSetUtil;
import io.github.auspis.fluentsql4j.dsl.util.ResultSetUtil.RowMapper;
import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.MysqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL;
import io.github.auspis.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.auspis.fluentsql4j.test.util.annotation.E2ETest;
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for MysqlDSLE2E with DSLRegistry and real MySQL database.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real MySQL database operations using Testcontainers.
 */
@E2ETest
@Testcontainers
class MysqlDSLE2E {

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static DSLRegistry registry;
    private static Connection connection;
    private static RowMapper<String> nameMapper;

    private MysqlDSL dsl;

    @BeforeAll
    static void beforeAll() throws Exception {
        registry = DSLRegistry.createWithServiceLoader();

        connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());

        TestDatabaseUtil.dropUsersTable(connection);
        TestDatabaseUtil.createUsersTableWithBackTicks(connection);
        TestDatabaseUtil.dropOrdersTable(connection);
        TestDatabaseUtil.createOrderTableWithBackTicks(connection);

        nameMapper = r -> r.getString("name");
    }

    @AfterAll
    static void afterAll() throws Exception {
        connection.close();
    }

    @BeforeEach
    void setUp() throws SQLException {
        dsl = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, MysqlDSL.class).orElseThrow();
        TestDatabaseUtil.truncateUsers(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
        TestDatabaseUtil.truncateOrders(connection);
        TestDatabaseUtil.insertSampleOrders(connection);
    }

    @Test
    void pluginDiscoveryOk() {
        Result<DSL> dslResult = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, MysqlDialectPlugin.DIALECT_VERSION);
        assertThat(dslResult).isInstanceOf(Result.Success.class);
        assertThat(dslResult.orElseThrow()).isNotNull().isInstanceOf(MysqlDSL.class);
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

        List<List<String>> results = ResultSetUtil.list(
                ps,
                r -> List.of(r.getString("name"), r.getBigDecimal("order_total").toPlainString()));

        List<List<String>> expected =
                List.of(List.of("Alice", "39.99"), List.of("Charlie", "49.99"), List.of("John Doe", "40.98"));

        assertThat(results).containsExactlyElementsOf(expected);
    }

    @Test
    void mergeStatementWithRealDatabase() throws SQLException {
        TestDatabaseUtil.createUsersUpdatesTableWithRecords(connection);
        // Build and execute MERGE statement using DSL
        try (var ps = dsl.mergeInto("users")
                .as("tgt")
                .using("users_updates", "src")
                .on("tgt", "id", "src", "id")
                .whenMatched()
                .set("name", ColumnReference.of("src", "name"))
                .set("email", ColumnReference.of("src", "email"))
                .set("age", ColumnReference.of("src", "age"))
                .set("active", ColumnReference.of("src", "active"))
                .set("birthdate", ColumnReference.of("src", "birthdate"))
                .set("createdAt", ColumnReference.of("src", "createdAt"))
                .set("address", ColumnReference.of("src", "address"))
                .set("preferences", ColumnReference.of("src", "preferences"))
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("name", ColumnReference.of("src", "name"))
                .set("email", ColumnReference.of("src", "email"))
                .set("age", ColumnReference.of("src", "age"))
                .set("active", ColumnReference.of("src", "active"))
                .set("birthdate", ColumnReference.of("src", "birthdate"))
                .set("createdAt", ColumnReference.of("src", "createdAt"))
                .set("address", ColumnReference.of("src", "address"))
                .set("preferences", ColumnReference.of("src", "preferences"))
                .build(connection)) {
            int affectedRows = ps.executeUpdate();
            // MySQL ON DUPLICATE KEY UPDATE returns affected rows count
            // 1 = inserted, 2 = updated (technically 1 deleted + 1 inserted in MySQL's logic)
            assertThat(affectedRows).isGreaterThan(0);
        }

        // Verify John Doe was updated
        try (var ps = connection.prepareStatement("SELECT * FROM users WHERE id = 1");
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(1);
            assertThat(rs.getString("name")).isEqualTo("John Doe");
            assertThat(rs.getString("email")).isEqualTo("john.newemail@example.com");
            assertThat(rs.getInt("age")).isEqualTo(31);
            assertThat(rs.getBoolean("active")).isTrue();
        }

        // Verify new user was inserted
        try (var ps = connection.prepareStatement("SELECT * FROM users WHERE id = 11");
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("id")).isEqualTo(11);
            assertThat(rs.getString("name")).isEqualTo("New User");
            assertThat(rs.getString("email")).isEqualTo("newuser@example.com");
            assertThat(rs.getInt("age")).isEqualTo(28);
            assertThat(rs.getBoolean("active")).isTrue();
        }

        // Verify total count (original 10 + 1 new = 11)
        try (var ps = connection.prepareStatement("SELECT COUNT(*) as cnt FROM users");
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt("cnt")).isEqualTo(11);
        }
    }

    @Test
    void groupConcatFunction() throws SQLException {
        RowMapper<List<String>> mapper = r -> List.of(r.getString("names"), r.getString("age"));
        List<List<String>> expected = List.of(
                List.of("Bob", "15"),
                List.of("Jane Smith, Diana", "25"),
                List.of("Grace", "28"),
                List.of("John Doe, Charlie, Henry", "30"),
                List.of("Alice, Frank", "35"),
                List.of("Eve", "40"));
        OrderByBuilder selectBuilder = dsl.select()
                .column("age")
                .groupConcat("name")
                .separator(", ")
                .as("names")
                .from("users")
                .groupBy()
                .column("age")
                .orderBy()
                .asc("age");

        // prepared statement
        try (PreparedStatement ps = selectBuilder.build(connection)) {
            assertThat(ResultSetUtil.list(ps, mapper)).containsAll(expected);
        }
    }

    @Test
    void ifFunction() throws SQLException {
        RowMapper<List<String>> mapper =
                r -> List.of(r.getString("name"), r.getString("age"), r.getString("age_group"));
        List<List<String>> expected = List.of(
                List.of("Alice", "35", "adult"),
                List.of("Bob", "15", "minor"),
                List.of("Charlie", "30", "adult"),
                List.of("Diana", "25", "adult"),
                List.of("Eve", "40", "adult"),
                List.of("Frank", "35", "adult"),
                List.of("Grace", "28", "adult"),
                List.of("Henry", "30", "adult"),
                List.of("Jane Smith", "25", "adult"),
                List.of("John Doe", "30", "adult"));
        var builder = dsl.select()
                .column("name")
                .column("age")
                .ifExpr()
                .when("users", "age")
                .gte(18)
                .then("adult")
                .otherwise("minor")
                .as("age_group")
                .from("users")
                .orderBy()
                .asc("name");

        // prepared statement
        try (PreparedStatement ps = builder.build(connection)) {
            assertThat(ResultSetUtil.list(ps, mapper)).containsAll(expected);
        }
    }

    // JSON Functions E2E Tests
    // Tests verify JSON functions work with real MySQL database using existing test data.
    // Users 8, 9, 10 have JSON data in address and preferences columns.

    @Test
    void jsonExistsChecksPathInJson() throws SQLException {
        // Users 8 (Frank), 9 (Grace), 10 (Henry) have address JSON with city field
        List<String> names = ResultSetUtil.list(
                dsl.select()
                        .column("name")
                        .from("users")
                        .where()
                        .jsonExists("address", "$.city")
                        .exists()
                        .orderBy()
                        .asc("id")
                        .build(connection),
                r -> r.getString("name"));

        assertThat(names).containsExactly("Frank", "Grace", "Henry");
    }

    @Test
    void jsonValueExtractsCityFromAddress() throws SQLException {
        // Extract city for user 8 (Frank - Milan)
        List<String> cities = ResultSetUtil.list(
                dsl.select()
                        .jsonValue("address", "$.city")
                        .as("city")
                        .from("users")
                        .where()
                        .column("id")
                        .eq(8)
                        .build(connection),
                r -> r.getString("city"));

        assertThat(cities).containsExactly("Milan");
    }

    @Test
    void jsonQueryExtractsFullObject() throws SQLException {
        // Extract full address object for user 9 (Grace - Rome)
        List<String> addresses = ResultSetUtil.list(
                dsl.select()
                        .jsonQuery("address", "$")
                        .as("addr")
                        .from("users")
                        .where()
                        .column("id")
                        .eq(9)
                        .build(connection),
                r -> r.getString("addr"));

        assertThat(addresses).hasSize(1);
        String address = addresses.get(0);
        assertThat(address).contains("Rome", "Via Torino 45", "00100", "Italy");
    }

    @Test
    void complexJsonQueryWithMultipleFunctions() throws SQLException {
        // Combine JSON_EXISTS, JSON_VALUE, WHERE with JSON conditions
        RowMapper<List<String>> mapper = r -> List.of(r.getString("name"), r.getString("city"));

        List<List<String>> results = ResultSetUtil.list(
                dsl.select()
                        .column("name")
                        .jsonValue("address", "$.city")
                        .as("city")
                        .from("users")
                        .where()
                        .jsonValue("address", "$.city")
                        .eq("Milan")
                        .or()
                        .jsonValue("address", "$.city")
                        .eq("Rome")
                        .orderBy()
                        .asc("city")
                        .build(connection),
                mapper);

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(r -> r.get(0), r -> r.get(1))
                .containsExactly(
                        org.assertj.core.api.Assertions.tuple("Frank", "Milan"),
                        org.assertj.core.api.Assertions.tuple("Grace", "Rome"));
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
    void groupConcatFunctionWithPreparedStatement() throws SQLException {
        // Use GROUP_CONCAT to concatenate names by age group
        var selectBuilder = dsl.select()
                .column("age")
                .groupConcat("name")
                .separator(", ")
                .as("names")
                .from("users")
                .groupBy()
                .column("age")
                .orderBy()
                .asc("age");

        // Execute and verify results
        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            boolean found30YearOlds = false;
            while (rs.next()) {
                int age = rs.getInt("age");
                String names = rs.getString("names");
                assertThat(names).isNotNull();

                // Verify 30-year-olds are concatenated
                if (age == 30) {
                    found30YearOlds = true;
                    // Should contain multiple names separated by ", "
                    assertThat(names).contains(", ");
                    assertThat(names.split(", ")).hasSizeGreaterThanOrEqualTo(2);
                }
            }
            assertThat(found30YearOlds).isTrue();
        }
    }

    @Test
    void dateFormatFunction() throws SQLException {
        // Get DSL from registry
        var dsl =
                registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, MysqlDSL.class).orElseThrow();

        // Use DATE_FORMAT fluent API to format birthdates
        var selectBuilder = dsl.select()
                .column("name")
                .dateFormat("birthdate", "%Y-%m-%d")
                .as("formatted_date")
                .from("users")
                .orderBy()
                .asc("name")
                .fetch(5);

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
                String name = rs.getString("name");
                String formattedDate = rs.getString("formatted_date");

                assertThat(name).isNotNull();
                assertThat(formattedDate).isNotNull();
                // Verify format is YYYY-MM-DD
                assertThat(formattedDate).matches("\\d{4}-\\d{2}-\\d{2}");
            }
            assertThat(count).isEqualTo(5);
        }
    }

    @Test
    void complexQueryWithMultipleMySQLFunctions() throws SQLException {
        // Combine multiple MySQL custom functions: GROUP_CONCAT and COUNT
        var selectBuilder = dsl.select()
                .column("age")
                .groupConcat("name")
                .separator(" | ")
                .as("users_list")
                .countStar()
                .as("user_count")
                .from("users")
                .where()
                .column("active")
                .eq(true)
                .groupBy()
                .column("age")
                .orderBy()
                .asc("age");

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            boolean foundResults = false;
            while (rs.next()) {
                foundResults = true;
                int age = rs.getInt("age");
                String usersList = rs.getString("users_list");
                int userCount = rs.getInt("user_count");

                assertThat(age).isGreaterThan(0);
                assertThat(usersList).isNotNull();
                assertThat(userCount).isGreaterThanOrEqualTo(1);

                // Verify GROUP_CONCAT with custom separator
                if (userCount > 1) {
                    assertThat(usersList).contains(" | ");
                }
            }
            assertThat(foundResults).isTrue();
        }
    }

    @Test
    void concatFunctionWithBuilder() throws SQLException {
        var selectBuilder = dsl.select()
                .concat()
                .column("name")
                .column("email")
                .as("name_email")
                .from("users")
                .where()
                .column("id")
                .eq(1);

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String result = rs.getString("name_email");
            assertThat(result).isEqualTo("John Doejohn@example.com");
        }
    }

    @Test
    void coalesceFunctionWithBuilder() throws SQLException {
        // COALESCE returns first non-NULL value
        var selectBuilder = dsl.select()
                .coalesce()
                .column("email")
                .column("name")
                .as("contact")
                .from("users")
                .where()
                .column("id")
                .eq(1);

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String result = rs.getString("contact");
            assertThat(result).isEqualTo("john@example.com");
        }
    }

    @Test
    void ifnullFunction() throws SQLException {
        // Add a row with NULL email for testing
        try (var stmt = connection.createStatement()) {
            stmt.execute(
                    "INSERT INTO users VALUES (99, 'Test User', NULL, 30, true, '1990-01-01', '2023-01-01', NULL, NULL)");
        }

        var selectBuilder = dsl.select()
                .column("name")
                .ifnull("email", "no-email@example.com")
                .as("contact_email")
                .from("users")
                .where()
                .column("id")
                .eq(99);

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String name = rs.getString("name");
            String email = rs.getString("contact_email");
            assertThat(name).isEqualTo("Test User");
            assertThat(email).isEqualTo("no-email@example.com");
        }
    }

    @Test
    void inheritedSumAggregateFunction() throws SQLException {
        var selectBuilder = dsl.select().sum("age").as("total_age").from("users");

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            int totalAge = rs.getInt("total_age");
            assertThat(totalAge).isEqualTo(293); // Sum of all ages: 30+25+15+35+30+25+40+35+28+30
        }
    }

    @Test
    void inheritedCountDistinctAggregateFunction() throws SQLException {
        var selectBuilder =
                dsl.select().countDistinct("age").as("distinct_ages").from("users");

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            int distinctAges = rs.getInt("distinct_ages");
            assertThat(distinctAges).isEqualTo(6); // Distinct ages: 15, 25, 28, 30, 35, 40
        }
    }

    @Test
    void ifFunctionWithComplexCondition() throws SQLException {
        var selectBuilder = dsl.select()
                .column("name")
                .ifExpr()
                .when("age")
                .gte(30)
                .then("Adult")
                .otherwise("Young")
                .as("age_category")
                .from("users")
                .orderBy()
                .asc("id");

        try (PreparedStatement ps = selectBuilder.build(connection);
                var rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
                String name = rs.getString("name");
                String category = rs.getString("age_category");

                assertThat(name).isNotNull();
                assertThat(category).isIn("Adult", "Young");

                // Verify specific cases
                if (name.equals("John Doe")) {
                    assertThat(category).isEqualTo("Adult"); // age = 30
                } else if (name.equals("Bob")) {
                    assertThat(category).isEqualTo("Young"); // age = 15
                }
            }
            assertThat(count).isEqualTo(10);
        }
    }
}
