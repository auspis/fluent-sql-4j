package lan.tlab.r4j.sql.e2e.plugin.builtin;

import static lan.tlab.r4j.sql.plugin.builtin.mysql.MySQLDialectPlugin.DIALECT_NAME;
import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.orderby.OrderBy;
import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.RegistryResult;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.sql.plugin.builtin.mysql.MySQLDialectPlugin;
import lan.tlab.r4j.sql.util.annotation.E2ETest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for MySQLDialectPlugin with SqlDialectRegistry and real MySQL database.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real MySQL database operations using Testcontainers.
 */
@E2ETest
@Testcontainers
class MySQLDialectPluginE2E {

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
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
        RegistryResult<SqlRenderer> result = registry.getRenderer(DIALECT_NAME, "8.0.35");

        assertThat(result).isInstanceOf(RegistryResult.Success.class);
        SqlRenderer renderer = result.orElseThrow();
        assertThat(renderer).isNotNull();
    }

    @Test
    void versionMatching() {
        // Should match MySQL 8.x versions (using ^8.0.0 range)
        RegistryResult<SqlRenderer> version800 = registry.getRenderer(DIALECT_NAME, "8.0.0");
        assertThat(version800).isInstanceOf(RegistryResult.Success.class);

        RegistryResult<SqlRenderer> version8035 = registry.getRenderer(DIALECT_NAME, "8.0.35");
        assertThat(version8035).isInstanceOf(RegistryResult.Success.class);

        RegistryResult<SqlRenderer> version810 = registry.getRenderer(DIALECT_NAME, "8.1.0");
        assertThat(version810).isInstanceOf(RegistryResult.Success.class);

        // Should NOT match MySQL 5.7 or 9.0
        RegistryResult<SqlRenderer> version57 = registry.getRenderer(DIALECT_NAME, "5.7.42");
        assertThat(version57).isInstanceOf(RegistryResult.Failure.class);

        RegistryResult<SqlRenderer> version90 = registry.getRenderer(DIALECT_NAME, "9.0.0");
        assertThat(version90).isInstanceOf(RegistryResult.Failure.class);
    }

    @Test
    void getRendererWithoutVersion() {
        // When version is not specified, should return available plugin
        RegistryResult<SqlRenderer> result = registry.getRenderer(DIALECT_NAME);

        assertThat(result).isInstanceOf(RegistryResult.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldProduceWorkingRenderer() throws SQLException {
        // Get renderer from registry
        RegistryResult<SqlRenderer> result = registry.getRenderer(DIALECT_NAME, "8.0.35");
        SqlRenderer renderer = result.orElseThrow();

        // Verify renderer works by generating MySQL-specific SQL
        assertThat(renderer).isNotNull();

        // Use the AST directly to verify MySQL-specific rendering
        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .build();

        String sql = statement.accept(renderer, new AstContext());

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
        SqlRenderer renderer = registry.getRenderer(DIALECT_NAME, "8.0.35").orElseThrow();

        // Use AST to verify MySQL syntax with backticks
        var statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .build();

        String sql = statement.accept(renderer, new AstContext());

        // MySQL uses backticks for identifier escaping
        assertThat(sql).contains("`");
        assertThat(sql).doesNotContain("\""); // Should not use double quotes
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`name`");
    }

    @Test
    void shouldGenerateMySQLPaginationSyntax() {
        // Get renderer from registry
        SqlRenderer renderer = registry.getRenderer(DIALECT_NAME, "8.0.35").orElseThrow();

        // Use AST to verify MySQL LIMIT/OFFSET syntax
        var statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "name"))))
                .fetch(Fetch.builder().rows(3).offset(5).build())
                .build();

        String sql = statement.accept(renderer, new AstContext());

        // MySQL uses LIMIT n OFFSET m syntax
        assertThat(sql).contains("LIMIT 3 OFFSET 5");
        // Should NOT use standard SQL OFFSET...ROWS FETCH syntax
        assertThat(sql).doesNotContain("OFFSET 5 ROWS");
        assertThat(sql).doesNotContain("FETCH NEXT");
    }

    @Test
    void shouldUseRendererForDifferentStatementTypes() throws SQLException {
        // Get renderer from registry
        SqlRenderer renderer = registry.getRenderer("MySQL", "8.0.35").orElseThrow();

        // Test SELECT with WHERE using the AST
        var selectStatement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "age"))))
                .from(From.fromTable("users"))
                .where(lan.tlab.r4j.sql.ast.clause.conditional.where.Where.of(
                        Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25))))
                .build();

        String selectSql = selectStatement.accept(renderer, new AstContext());
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
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();
        SqlDialectPluginRegistry newRegistry = emptyRegistry.register(plugin);

        // Verify it's now available
        assertThat(newRegistry.isSupported(DIALECT_NAME)).isTrue();
        RegistryResult<SqlRenderer> result = newRegistry.getRenderer(DIALECT_NAME, "8.0.35");
        assertThat(result).isInstanceOf(RegistryResult.Success.class);
    }

    @Test
    void shouldExecuteMySQLSpecificQueries() throws SQLException {
        // Get renderer from registry
        SqlRenderer renderer = registry.getRenderer(DIALECT_NAME, "8.0.35").orElseThrow();

        // Create a query using the AST
        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .where(lan.tlab.r4j.sql.ast.clause.conditional.where.Where.of(
                        Comparison.gte(ColumnReference.of("users", "age"), Literal.of(25))))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "name"))))
                .fetch(Fetch.builder().rows(5).offset(0).build())
                .build();

        String sql = statement.accept(renderer, new AstContext());

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
}
