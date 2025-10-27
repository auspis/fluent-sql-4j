package lan.tlab.r4j.sql.e2e.plugin.builtin;

import static lan.tlab.r4j.sql.plugin.builtin.postgresql.PostgreSQLDialectPlugin.DIALECT_NAME;
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
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.functional.Result;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.sql.plugin.builtin.postgresql.PostgreSQLDialectPlugin;
import lan.tlab.r4j.sql.util.annotation.E2ETest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * End-to-end tests for PostgreSQLDialectPlugin with SqlDialectRegistry and real PostgreSQL database.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real PostgreSQL database operations using Testcontainers.
 */
@E2ETest
@Testcontainers
class PostgreSQLDialectPluginE2E {

    @Container
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private SqlDialectPluginRegistry registry;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create registry with ServiceLoader to test plugin discovery
        registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Set up PostgreSQL database for renderer functionality tests
        connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        createPostgreSQLUsersTable(connection);
        insertPostgreSQLSampleUsers(connection);
    }

    private void createPostgreSQLUsersTable(Connection connection) throws SQLException {
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

    private void insertPostgreSQLSampleUsers(Connection connection) throws SQLException {
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
        assertThat(registry.isSupported("postgresql")).isTrue(); // case-insensitive
        assertThat(registry.isSupported("POSTGRESQL")).isTrue();
    }

    @Test
    void getRenderer() {
        Result<DialectRenderer> result = registry.getDialectRenderer(DIALECT_NAME, "14.5.0");

        assertThat(result).isInstanceOf(Result.Success.class);
        DialectRenderer renderer = result.orElseThrow();
        assertThat(renderer).isNotNull();
    }

    @Test
    void versionMatching() {
        // Should match PostgreSQL 12.x, 13.x, 14.x, 15.x, 16.x versions (using >=12.0.0 <17.0.0 range)
        Result<DialectRenderer> version12 = registry.getDialectRenderer(DIALECT_NAME, "12.0.0");
        assertThat(version12).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version13 = registry.getDialectRenderer(DIALECT_NAME, "13.5.0");
        assertThat(version13).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version14 = registry.getDialectRenderer(DIALECT_NAME, "14.5.0");
        assertThat(version14).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version15 = registry.getDialectRenderer(DIALECT_NAME, "15.2.0");
        assertThat(version15).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> version16 = registry.getDialectRenderer(DIALECT_NAME, "16.0.0");
        assertThat(version16).isInstanceOf(Result.Success.class);

        // Should NOT match PostgreSQL 11.x or 17.x
        Result<DialectRenderer> version11 = registry.getDialectRenderer(DIALECT_NAME, "11.20.0");
        assertThat(version11).isInstanceOf(Result.Failure.class);

        Result<DialectRenderer> version17 = registry.getDialectRenderer(DIALECT_NAME, "17.0.0");
        assertThat(version17).isInstanceOf(Result.Failure.class);
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
        Result<DialectRenderer> result = registry.getDialectRenderer(DIALECT_NAME, "14.5.0");
        DialectRenderer renderer = result.orElseThrow();

        // Verify renderer works by generating PostgreSQL-specific SQL
        assertThat(renderer).isNotNull();

        // Use the AST directly to verify PostgreSQL-specific rendering
        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .build();

        String sql = renderer.renderSql(statement);

        // Should use PostgreSQL double quotes
        assertThat(sql).contains("\"users\"");
        assertThat(sql).contains("\"name\"");
        assertThat(sql).contains("\"email\"");

        // Execute the query to verify it works with PostgreSQL
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
    void shouldGeneratePostgreSQLSyntaxWithDoubleQuotes() throws SQLException {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer(DIALECT_NAME, "14.5.0").orElseThrow();

        // Use AST to verify PostgreSQL syntax with double quotes
        var statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .build();

        String sql = renderer.renderSql(statement);

        // PostgreSQL uses double quotes for identifier escaping
        assertThat(sql).contains("\"");
        assertThat(sql).doesNotContain("`"); // Should not use backticks
        assertThat(sql).contains("\"users\"");
        assertThat(sql).contains("\"name\"");
    }

    @Test
    void shouldGeneratePostgreSQLPaginationSyntax() {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer(DIALECT_NAME, "14.5.0").orElseThrow();

        // Use AST to verify PostgreSQL LIMIT/OFFSET syntax
        var statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "name"))))
                .fetch(new Fetch(5, 3))
                .build();

        String sql = renderer.renderSql(statement);

        // PostgreSQL uses LIMIT n OFFSET m syntax
        assertThat(sql).contains("LIMIT 3 OFFSET 5");
        // Should NOT use standard SQL OFFSET...ROWS FETCH syntax
        assertThat(sql).doesNotContain("OFFSET 5 ROWS");
        assertThat(sql).doesNotContain("FETCH NEXT");
    }

    @Test
    void shouldUseRendererForDifferentStatementTypes() throws SQLException {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer("PostgreSQL", "14.5.0").orElseThrow();

        // Test SELECT with WHERE using the AST
        var selectStatement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "age"))))
                .from(From.fromTable("users"))
                .where(lan.tlab.r4j.sql.ast.clause.conditional.where.Where.of(
                        Comparison.gt(ColumnReference.of("users", "age"), Literal.of(25))))
                .build();

        String selectSql = renderer.renderSql(selectStatement);
        assertThat(selectSql).contains("WHERE");
        assertThat(selectSql).contains("\"users\"");
        assertThat(selectSql).contains("\"name\"");
        assertThat(selectSql).contains("\"age\"");
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
        // Verify PostgreSQL plugin coexists with other plugins (e.g., StandardSQL, MySQL)
        assertThat(registry.isEmpty()).isFalse();
        assertThat(registry.size()).isGreaterThanOrEqualTo(3);

        // Verify we can get PostgreSQL specifically
        assertThat(registry.getSupportedDialects()).contains("postgresql");
    }

    @Test
    void shouldWorkWithRegistryManualRegistration() {
        // Create a new empty registry and manually register the plugin
        SqlDialectPluginRegistry emptyRegistry = SqlDialectPluginRegistry.empty();
        assertThat(emptyRegistry.isSupported(DIALECT_NAME)).isFalse();

        // Register the plugin
        SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();
        SqlDialectPluginRegistry newRegistry = emptyRegistry.register(plugin);

        // Verify it's now available
        assertThat(newRegistry.isSupported(DIALECT_NAME)).isTrue();
        Result<DialectRenderer> result = newRegistry.getDialectRenderer(DIALECT_NAME, "14.5.0");
        assertThat(result).isInstanceOf(Result.Success.class);
    }

    @Test
    void shouldExecutePostgreSQLSpecificQueries() throws SQLException {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer(DIALECT_NAME, "14.5.0").orElseThrow();

        // Create a query using the AST
        var statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .where(lan.tlab.r4j.sql.ast.clause.conditional.where.Where.of(
                        Comparison.gte(ColumnReference.of("users", "age"), Literal.of(25))))
                .orderBy(OrderBy.of(Sorting.asc(ColumnReference.of("users", "name"))))
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
}
