package lan.tlab.r4j.integration.sql.plugin.builtin;

import static lan.tlab.r4j.sql.plugin.builtin.MySQLDialectPlugin.DIALECT_NAME;
import static lan.tlab.r4j.sql.plugin.builtin.MySQLDialectPlugin.DIALECT_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.clause.selection.Select;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.statement.dql.SelectStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.dsl.util.ResultSetUtil;
import lan.tlab.r4j.sql.plugin.RegistryResult;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectRegistry;
import lan.tlab.r4j.sql.plugin.builtin.MySQLDialectPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Integration tests for MySQLDialectPlugin with SqlDialectRegistry.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real MySQL database operations using Testcontainers.
 */
@Testcontainers
class MySQLDialectPluginIntegrationTest {

    @Container
    @SuppressWarnings("resource")
    private static final MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private SqlDialectRegistry registry;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create registry with ServiceLoader to test plugin discovery
        registry = SqlDialectRegistry.createWithServiceLoader();

        // Set up MySQL database for renderer functionality tests
        connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        createUsersTable(connection);
        insertSampleUsers(connection);
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
        RegistryResult<SqlRenderer> result = registry.getRenderer(DIALECT_NAME, DIALECT_VERSION);

        assertThat(result).isInstanceOf(RegistryResult.Success.class);
        SqlRenderer renderer = result.orElseThrow();
        assertThat(renderer).isNotNull();
    }

    @Test
    void versionMatching() {
        // Empty version should match when no version is specified
        RegistryResult<SqlRenderer> emptyVersionMatch = registry.getRenderer(DIALECT_NAME, DIALECT_VERSION);
        assertThat(emptyVersionMatch).isInstanceOf(RegistryResult.Success.class);

        // Should also work without specifying version
        RegistryResult<SqlRenderer> noVersion = registry.getRenderer(DIALECT_NAME);
        assertThat(noVersion).isInstanceOf(RegistryResult.Success.class);
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
        RegistryResult<SqlRenderer> result = registry.getRenderer(DIALECT_NAME, DIALECT_VERSION);
        SqlRenderer renderer = result.orElseThrow();

        // Build query using AST directly
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .build();

        String sql = statement.accept(renderer, new AstContext());

        // Verify MySQL-specific backtick escaping
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`name`");
        assertThat(sql).contains("`email`");

        // Execute the query to verify it works with MySQL
        List<List<Object>> rows = ResultSetUtil.list(
                connection.prepareStatement(sql), r -> List.of(r.getString("name"), r.getString("email")));

        assertThat(rows)
                .hasSize(10)
                .extracting(r -> (String) r.get(0), r -> (String) r.get(1))
                .contains(tuple("John Doe", "john@example.com"), tuple("Jane Smith", "jane@example.com"));
    }

    @Test
    void shouldGenerateMySQLPaginationSyntax() {
        // Get renderer from registry
        SqlRenderer renderer = registry.getRenderer(DIALECT_NAME, DIALECT_VERSION).orElseThrow();

        // Verify it generates MySQL-specific LIMIT/OFFSET syntax for pagination
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .fetch(Fetch.builder().rows(3).offset(5).build())
                .build();

        String paginationSql = statement.accept(renderer, new AstContext());

        // MySQL uses LIMIT and OFFSET, not the SQL:2008 OFFSET...ROWS FETCH...ROWS syntax
        assertThat(paginationSql).contains("LIMIT 3 OFFSET 5");
        assertThat(paginationSql).doesNotContain("OFFSET 5 ROWS");
        assertThat(paginationSql).doesNotContain("FETCH NEXT");
    }

    @Test
    void shouldUseMySQLBacktickEscaping() {
        // Get renderer from registry
        SqlRenderer renderer = registry.getRenderer(DIALECT_NAME, DIALECT_VERSION).orElseThrow();

        // Verify it uses MySQL backtick escaping instead of double quotes
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .build();

        String sql = statement.accept(renderer, new AstContext());

        // MySQL uses backticks for identifier escaping
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`name`");
        // Should not use double quotes for identifiers (that's standard SQL)
        assertThat(sql).doesNotContain("\"users\"");
        assertThat(sql).doesNotContain("\"name\"");
    }

    @Test
    void shouldUseRendererForDifferentOperations() throws SQLException {
        // Get renderer from registry
        SqlRenderer renderer = registry.getRenderer("MySQL", "^8.0.0").orElseThrow();

        // Verify renderer is properly configured for MySQL
        assertThat(renderer).isNotNull();

        // Test simple SELECT to verify renderer works
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .build();

        String sql = statement.accept(renderer, new AstContext());
        assertThat(sql).contains("SELECT");
        assertThat(sql).contains("`users`");
        assertThat(sql).contains("`name`");
    }

    @Test
    void shouldBeIntegratedWithMultiplePlugins() {
        // Verify MySQL plugin coexists with other plugins (StandardSQL)
        assertThat(registry.isEmpty()).isFalse();
        assertThat(registry.size()).isGreaterThanOrEqualTo(2);

        // Verify we can get MySQL specifically
        assertThat(registry.getSupportedDialects()).contains("mysql");
        assertThat(registry.getSupportedDialects()).contains("standardsql");
    }

    @Test
    void shouldWorkWithRegistryManualRegistration() {
        // Create a new empty registry and manually register the plugin
        SqlDialectRegistry emptyRegistry = SqlDialectRegistry.empty();
        assertThat(emptyRegistry.isSupported(DIALECT_NAME)).isFalse();

        // Register the plugin
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();
        SqlDialectRegistry newRegistry = emptyRegistry.register(plugin);

        // Verify it's now available
        assertThat(newRegistry.isSupported(DIALECT_NAME)).isTrue();
        RegistryResult<SqlRenderer> result = newRegistry.getRenderer(DIALECT_NAME, DIALECT_VERSION);
        assertThat(result).isInstanceOf(RegistryResult.Success.class);
    }

    @Test
    void shouldExecuteRealQueriesOnMySQL() throws SQLException {
        SqlRenderer renderer = registry.getRenderer("MySQL").orElseThrow();

        // Execute a real query using MySQL-specific syntax
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(
                        new ScalarExpressionProjection(ColumnReference.of("users", "name")),
                        new ScalarExpressionProjection(ColumnReference.of("users", "email"))))
                .from(From.fromTable("users"))
                .build();

        String sql = statement.accept(renderer, new AstContext());

        List<List<Object>> results =
                ResultSetUtil.list(connection.prepareStatement(sql), r -> List.of(r.getString("name"), r.getString("email")));

        assertThat(results).isNotEmpty();
        assertThat(results).extracting(r -> (String) r.get(0)).contains("John Doe", "Alice", "Eve", "Frank", "Henry");
    }

    @Test
    void shouldHandleMySQLPaginationInRealDatabase() throws SQLException {
        SqlRenderer renderer = registry.getRenderer("MySQL").orElseThrow();

        // Test pagination with real MySQL database
        SelectStatement statement = SelectStatement.builder()
                .select(Select.of(new ScalarExpressionProjection(ColumnReference.of("users", "name"))))
                .from(From.fromTable("users"))
                .fetch(Fetch.builder().rows(3).offset(2).build())
                .build();

        String sql = statement.accept(renderer, new AstContext());

        List<String> results = ResultSetUtil.list(connection.prepareStatement(sql), r -> r.getString("name"));

        // Should return 3 results starting from offset 2
        assertThat(results).hasSize(3);
    }

    /**
     * Creates a users table in MySQL database.
     */
    private void createUsersTable(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Drop table if it exists to ensure clean state
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
                        createdAt TIMESTAMP
                    )
                    """);
        }
    }

    /**
     * Inserts sample users into the MySQL database.
     */
    private void insertSampleUsers(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(
                    "INSERT INTO users VALUES (1, 'John Doe', 'john@example.com', 30, true, '1990-01-01', '2023-01-01 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (2, 'Jane Smith', 'jane@example.com', 25, true, '1995-01-01', '2023-01-01 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (3, 'Bob', 'bob@example.com', 15, false, '2005-01-01', '2023-01-01 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (4, 'Alice', 'alice@example.com', 35, true, '1990-01-01', '2023-01-01 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (5, 'Charlie', 'charlie@example.com', 30, true, '1991-01-01', '2023-01-02 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (6, 'Diana', 'diana@example.com', 25, false, '1996-01-01', '2023-01-03 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (7, 'Eve', 'eve@example.com', 40, true, '1985-01-01', '2023-01-04 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (8, 'Frank', 'frank@example.com', 35, true, '1990-02-01', '2023-01-05 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (9, 'Grace', 'grace@example.com', 28, false, '1997-01-01', '2023-01-06 00:00:00')");
            stmt.execute(
                    "INSERT INTO users VALUES (10, 'Henry', 'henry@example.com', 30, true, '1995-01-01', '2023-01-07 00:00:00')");
        }
    }
}
