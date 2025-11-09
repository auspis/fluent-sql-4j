package lan.tlab.r4j.integration.sql.plugin.builtin;

import static lan.tlab.r4j.sql.plugin.builtin.mysql.MysqlDialectPlugin.DIALECT_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.dql.clause.Fetch;
import lan.tlab.r4j.sql.ast.dql.clause.From;
import lan.tlab.r4j.sql.ast.dql.clause.Select;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.dql.statement.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.functional.Result;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.sql.plugin.builtin.mysql.MysqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.MysqlDSL;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for MySQLDialectPlugin with SqlDialectRegistry and real MySQL database.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real MySQL database operations using Testcontainers.
 */
@Testcontainers
class MysqlDialectPluginIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("test-db")
            .withUsername("test")
            .withPassword("test");

    private SqlDialectPluginRegistry registry;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create registry with ServiceLoader to test plugin discovery
        registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Set up MySQL database for renderer functionality tests
        connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        createMySQLUsersTable(connection);
        insertMySQLSampleUsers(connection);
    }

    private void createMySQLUsersTable(Connection connection) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS users");
            stmt.execute(
                    """
                    CREATE TABLE users (
                    id INTEGER PRIMARY KEY,
                    name VARCHAR(50),
                    email VARCHAR(100),
                    age INTEGER,
                    active BOOLEAN,
                    birthdate DATE,
                    createdAt TIMESTAMP)
                    """);
        }
    }

    private void insertMySQLSampleUsers(Connection connection) throws SQLException {
        try (var stmt = connection.createStatement()) {
            stmt.execute(
                    "INSERT INTO users VALUES (1, 'John Doe', 'john@example.com', 30, true, '1990-01-01', '2023-01-01')");
            stmt.execute(
                    "INSERT INTO users VALUES (2, 'Jane Smith', 'jane@example.com', 25, true, '1995-01-01', '2023-01-01')");
            stmt.execute(
                    "INSERT INTO users VALUES (3, 'Bob', 'bob@example.com', 15, false, '2005-01-01', '2023-01-01')");
            stmt.execute(
                    "INSERT INTO users VALUES (4, 'Alice', 'alice@example.com', 35, true, '1990-01-01', '2023-01-01')");
            stmt.execute(
                    "INSERT INTO users VALUES (5, 'Charlie', 'charlie@example.com', 30, true, '1991-01-01', '2023-01-02')");
            stmt.execute(
                    "INSERT INTO users VALUES (6, 'Diana', 'diana@example.com', 25, false, '1996-01-01', '2023-01-03')");
            stmt.execute(
                    "INSERT INTO users VALUES (7, 'Eve', 'eve@example.com', 40, true, '1985-01-01', '2023-01-04')");
            stmt.execute(
                    "INSERT INTO users VALUES (8, 'Frank', 'frank@example.com', 35, true, '1990-02-01', '2023-01-05')");
            stmt.execute(
                    "INSERT INTO users VALUES (9, 'Grace', 'grace@example.com', 28, false, '1997-01-01', '2023-01-06')");
            stmt.execute(
                    "INSERT INTO users VALUES (10, 'Henry', 'henry@example.com', 30, true, '1995-01-01', '2023-01-07')");
        }
    }

    @AfterEach
    void tearDown() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    void registration() {
        assertThat(registry.isSupported(DIALECT_NAME)).isTrue();
        assertThat(registry.isSupported("mysql")).isTrue(); // case-insensitive
        assertThat(registry.isSupported("MYSQL")).isTrue();
    }

    @Test
    void getRenderer() {
        Result<DialectRenderer> result = registry.getDialectRenderer(DIALECT_NAME, "8.0.35");

        assertThat(result).isInstanceOf(Result.Success.class);
        DialectRenderer renderer = result.orElseThrow();
        assertThat(renderer).isNotNull();
    }

    @Test
    void versionMatching() {
        // Should match MySQL 8.x versions (using ^8.0.0 range)
        Result<DialectRenderer> version800 = registry.getDialectRenderer(DIALECT_NAME, "8.0.0");
        assertThat(version800).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version8035 = registry.getDialectRenderer(DIALECT_NAME, "8.0.35");
        assertThat(version8035).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version810 = registry.getDialectRenderer(DIALECT_NAME, "8.1.0");
        assertThat(version810).isInstanceOf(Result.Success.class);

        // Should NOT match MySQL 5.7 or 9.0
        Result<DialectRenderer> version57 = registry.getDialectRenderer(DIALECT_NAME, "5.7.42");
        assertThat(version57).isInstanceOf(Result.Failure.class);

        Result<DialectRenderer> version90 = registry.getDialectRenderer(DIALECT_NAME, "9.0.0");
        assertThat(version90).isInstanceOf(Result.Failure.class);
    }

    @Test
    void getRendererWithoutVersion() {
        // When version is not specified, should return available plugin
        Result<DialectRenderer> result = registry.getRenderer(DIALECT_NAME);

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldProduceWorkingRenderer() throws SQLException {
        // Get renderer from registry
        Result<DialectRenderer> result = registry.getDialectRenderer(DIALECT_NAME, "8.0.35");
        DialectRenderer renderer = result.orElseThrow();

        // Verify renderer works by generating MySQL-specific SQL
        assertThat(renderer).isNotNull();

        // Use the AST directly to verify MySQL-specific rendering
        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .build();

        String sql = renderer.renderSql(statement);

        // Should use MySQL backticks
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`name`");
        assertThat(sql).contains("`email`");

        // Execute the query to verify it works with MySQL
        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
                assertThat(rs.getString("name")).isNotNull();
                assertThat(rs.getString("email")).isNotNull();
            }
            assertThat(count).isEqualTo(10);
        }
    }

    @Test
    void shouldGenerateMySQLSyntaxWithBackticks() throws SQLException {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer(DIALECT_NAME, "8.0.35").orElseThrow();

        // Use AST to verify MySQL syntax with backticks
        var statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .build();

        String sql = renderer.renderSql(statement);

        // MySQL uses backticks for identifier escaping
        assertThat(sql).contains("`");
        assertThat(sql).doesNotContain("\""); // Should not use double quotes
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`name`");
    }

    @Test
    void shouldGenerateMySQLPaginationSyntax() {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer(DIALECT_NAME, "8.0.35").orElseThrow();

        // Use AST to verify MySQL LIMIT/OFFSET syntax
        var statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .orderBy(lan.tlab.r4j.sql.ast.dql.clause.OrderBy.of(
                        lan.tlab.r4j.sql.ast.dql.clause.Sorting.asc(ColumnReference.of("users", "name"))))
                .fetch(new Fetch(5, 3))
                .build();

        String sql = renderer.renderSql(statement);

        // MySQL uses LIMIT n OFFSET m syntax
        assertThat(sql).contains("LIMIT 3 OFFSET 5");
        // Should NOT use standard SQL OFFSET...ROWS FETCH syntax
        assertThat(sql).doesNotContain("OFFSET 5 ROWS");
        assertThat(sql).doesNotContain("FETCH NEXT");
    }

    @Test
    void shouldUseRendererForDifferentStatementTypes() throws SQLException {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer("MySQL", "8.0.35").orElseThrow();

        // Test SELECT with WHERE using the AST
        var selectStatement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "age"))))
                .from(From.fromTable("users"))
                .where(lan.tlab.r4j.sql.ast.dql.clause.Where.of(lan.tlab.r4j.sql.ast.common.predicate.Comparison.gt(
                        ColumnReference.of("users", "age"),
                        lan.tlab.r4j.sql.ast.common.expression.scalar.Literal.of(25))))
                .build();

        String selectSql = renderer.renderSql(selectStatement);
        assertThat(selectSql).contains("WHERE");
        assertThat(selectSql).contains("`users`");
        assertThat(selectSql).contains("`name`");
        assertThat(selectSql).contains("`age`");
        assertThat(selectSql).isNotEmpty();

        // Execute to verify it works
        try (var ps = connection.prepareStatement(selectSql);
                var rs = ps.executeQuery()) {
            boolean hasResults = false;
            while (rs.next()) {
                hasResults = true;
                assertThat(rs.getString("name")).isNotNull();
                assertThat(rs.getInt("age")).isGreaterThan(25);
            }
            assertThat(hasResults).isTrue();
        }
    }

    @Test
    void shouldBeIntegratedWithMultiplePlugins() {
        // Verify MySQL plugin coexists with other plugins (e.g., StandardSQL)
        assertThat(registry.isEmpty()).isFalse();
        assertThat(registry.size()).isGreaterThanOrEqualTo(2);

        // Verify we can get MySQL specifically
        assertThat(registry.getSupportedDialects()).contains("mysql");
    }

    @Test
    void shouldWorkWithRegistryManualRegistration() {
        // Create a new empty registry and manually register the plugin
        SqlDialectPluginRegistry emptyRegistry = SqlDialectPluginRegistry.empty();
        assertThat(emptyRegistry.isSupported(DIALECT_NAME)).isFalse();

        // Register the plugin
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();
        SqlDialectPluginRegistry newRegistry = emptyRegistry.register(plugin);

        // Verify it's now available
        assertThat(newRegistry.isSupported(DIALECT_NAME)).isTrue();
        Result<DialectRenderer> result = newRegistry.getDialectRenderer(DIALECT_NAME, "8.0.35");
        assertThat(result).isInstanceOf(Result.Success.class);
    }

    @Test
    void shouldExecuteMySQLSpecificQueries() throws SQLException {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer(DIALECT_NAME, "8.0.35").orElseThrow();

        // Create a query using the AST
        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .where(lan.tlab.r4j.sql.ast.dql.clause.Where.of(lan.tlab.r4j.sql.ast.common.predicate.Comparison.gte(
                        ColumnReference.of("users", "age"),
                        lan.tlab.r4j.sql.ast.common.expression.scalar.Literal.of(25))))
                .orderBy(lan.tlab.r4j.sql.ast.dql.clause.OrderBy.of(
                        lan.tlab.r4j.sql.ast.dql.clause.Sorting.asc(ColumnReference.of("users", "name"))))
                .fetch(new Fetch(0, 5))
                .build();

        String sql = renderer.renderSql(statement);

        // Execute and verify results
        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
                assertThat(rs.getString("name")).isNotNull();
                assertThat(rs.getString("email")).isNotNull();
            }
            // Verify we got results limited to 5
            assertThat(count).isLessThanOrEqualTo(5);
            assertThat(count).isGreaterThan(0);
        }
    }

    @Test
    void shouldExecuteMySQLGroupConcatFunction() throws SQLException {
        // Get DSL from registry
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        // Use GROUP_CONCAT to concatenate names by age group
        String sql = dsl.select()
                .column("age")
                .groupConcat("name")
                .separator(", ")
                .as("names")
                .from("users")
                .groupBy("age")
                .orderBy("age")
                .build();

        // Verify SQL contains GROUP_CONCAT with SEPARATOR
        assertThat(sql).containsIgnoringCase("GROUP_CONCAT");
        assertThat(sql).containsIgnoringCase("SEPARATOR");

        // Execute and verify results
        try (var ps = connection.prepareStatement(sql);
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
    void shouldExecuteMySQLIfFunction() throws SQLException {
        // Get DSL from registry
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        // Use IF fluent API to categorize users by age (age >= 18 ? 'adult' : 'minor')
        String sql = dsl.select()
                .column("name")
                .column("age")
                .ifExpr()
                .when("users", "age")
                .gte(18)
                .then("adult")
                .otherwise("minor")
                .as("age_group")
                .from("users")
                .orderBy("name")
                .build();

        // Verify SQL contains IF function
        assertThat(sql).containsIgnoringCase("IF");

        // Execute and verify results
        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            boolean foundAdult = false;
            boolean foundMinor = false;

            while (rs.next()) {
                int age = rs.getInt("age");
                String ageGroup = rs.getString("age_group");

                assertThat(ageGroup).isIn("adult", "minor");

                // Verify logic: age >= 18 should be 'adult', otherwise 'minor'
                if (age >= 18) {
                    assertThat(ageGroup).isEqualTo("adult");
                    foundAdult = true;
                } else {
                    assertThat(ageGroup).isEqualTo("minor");
                    foundMinor = true;
                }
            }

            // Verify we found both categories
            assertThat(foundAdult).isTrue();
            assertThat(foundMinor).isTrue();
        }
    }

    @Test
    void shouldExecuteMySQLDateFormatFunction() throws SQLException {
        // Get DSL from registry
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        // Use DATE_FORMAT fluent API to format birthdates
        String sql = dsl.select()
                .column("name")
                .dateFormat("birthdate", "%Y-%m-%d")
                .as("formatted_date")
                .from("users")
                .orderBy("name")
                .fetch(5)
                .build();

        // Verify SQL contains DATE_FORMAT
        assertThat(sql).containsIgnoringCase("DATE_FORMAT");

        // Execute and verify results
        try (var ps = connection.prepareStatement(sql);
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
    void shouldExecuteComplexQueryWithMultipleMySQLFunctions() throws SQLException {
        // Get DSL from registry
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        // Combine multiple MySQL custom functions: GROUP_CONCAT and COUNT
        String sql = dsl.select()
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
                .groupBy("age")
                .orderBy("age")
                .build();

        // Verify SQL contains custom functions
        assertThat(sql).containsIgnoringCase("GROUP_CONCAT");
        assertThat(sql).containsIgnoringCase("COUNT");

        // Execute and verify results
        try (var ps = connection.prepareStatement(sql);
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
    void shouldExecuteConcatFunctionWithBuilder() throws SQLException {
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        String sql = dsl.select()
                .concat()
                .column("name")
                .column("email")
                .as("name_email")
                .from("users")
                .where()
                .column("id")
                .eq(1)
                .build();

        assertThat(sql).containsIgnoringCase("CONCAT");

        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String result = rs.getString("name_email");
            assertThat(result).isEqualTo("John Doejohn@example.com");
        }
    }

    @Test
    void shouldExecuteCoalesceFunctionWithBuilder() throws SQLException {
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        // COALESCE returns first non-NULL value
        String sql = dsl.select()
                .coalesce()
                .column("email")
                .column("name")
                .as("contact")
                .from("users")
                .where()
                .column("id")
                .eq(1)
                .build();

        assertThat(sql).containsIgnoringCase("COALESCE");

        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String result = rs.getString("contact");
            assertThat(result).isEqualTo("john@example.com");
        }
    }

    @Test
    void shouldExecuteIfnullFunction() throws SQLException {
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        // Add a row with NULL email for testing
        try (var stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO users VALUES (99, 'Test User', NULL, 30, true, '1990-01-01', '2023-01-01')");
        }

        String sql = dsl.select()
                .column("name")
                .ifnull("email", "no-email@example.com")
                .as("contact_email")
                .from("users")
                .where()
                .column("id")
                .eq(99)
                .build();

        assertThat(sql).containsIgnoringCase("IFNULL");

        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            String name = rs.getString("name");
            String email = rs.getString("contact_email");
            assertThat(name).isEqualTo("Test User");
            assertThat(email).isEqualTo("no-email@example.com");
        }

        // Clean up test data
        try (var stmt = connection.createStatement()) {
            stmt.execute("DELETE FROM users WHERE id = 99");
        }
    }

    @Test
    void shouldExecuteInheritedSumAggregateFunction() throws SQLException {
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        String sql = dsl.select().sum("age").as("total_age").from("users").build();

        assertThat(sql).containsIgnoringCase("SUM");

        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            int totalAge = rs.getInt("total_age");
            assertThat(totalAge).isEqualTo(293); // Sum of all ages: 30+25+15+35+30+25+40+35+28+30
        }
    }

    @Test
    void shouldExecuteInheritedCountDistinctAggregateFunction() throws SQLException {
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        String sql = dsl.select()
                .countDistinct("age")
                .as("distinct_ages")
                .from("users")
                .build();

        assertThat(sql).containsIgnoringCase("COUNT");
        assertThat(sql).containsIgnoringCase("DISTINCT");

        try (var ps = connection.prepareStatement(sql);
                var rs = ps.executeQuery()) {
            assertThat(rs.next()).isTrue();
            int distinctAges = rs.getInt("distinct_ages");
            assertThat(distinctAges).isEqualTo(6); // Distinct ages: 15, 25, 28, 30, 35, 40
        }
    }

    @Test
    void shouldExecuteIfFunctionWithComplexCondition() throws SQLException {
        var dsl = (MysqlDSL) registry.getDsl(DIALECT_NAME, "8.0.35").orElseThrow();

        String sql = dsl.select()
                .column("name")
                .ifExpr()
                .when("age")
                .gte(30)
                .then("Adult")
                .otherwise("Young")
                .as("age_category")
                .from("users")
                .orderBy("id")
                .build();

        assertThat(sql).containsIgnoringCase("IF");

        try (var ps = connection.prepareStatement(sql);
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
