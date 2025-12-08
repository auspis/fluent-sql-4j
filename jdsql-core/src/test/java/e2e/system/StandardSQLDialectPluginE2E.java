package e2e.system;

import static lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_NAME;
import static lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.util.ResultSetUtil;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import lan.tlab.r4j.jdsql.test.util.TestDatabaseUtil;
import lan.tlab.r4j.jdsql.test.util.annotation.E2ETest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * End-to-end tests for StandardSQLDialectPlugin with SqlDialectRegistry.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real database operations.
 */
@E2ETest
class StandardSQLDialectPluginE2E {

    private SqlDialectPluginRegistry registry;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create registry with ServiceLoader to test plugin discovery
        registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Set up database for renderer functionality tests
        connection = TestDatabaseUtil.createH2Connection();
        TestDatabaseUtil.createUsersTable(connection);
        TestDatabaseUtil.insertSampleUsers(connection);
    }

    @AfterEach
    void tearDown() throws SQLException {
        TestDatabaseUtil.closeConnection(connection);
    }

    @Test
    void registration() {
        assertThat(registry.isSupported(DIALECT_NAME)).isTrue();
        assertThat(registry.isSupported("standardsql")).isTrue(); // case-insensitive
        assertThat(registry.isSupported("STANDARDSQL")).isTrue();
    }

    @Test
    void getRenderer() {
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION);

        assertThat(result).isInstanceOf(Result.Success.class);
        PreparedStatementSpecFactory specFactory = result.orElseThrow();
        assertThat(specFactory).isNotNull();
    }

    @Test
    void versionMatching() {
        Result<PreparedStatementSpecFactory> exactMatch = registry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION);
        assertThat(exactMatch).isInstanceOf(Result.Success.class);

        Result<PreparedStatementSpecFactory> wrongVersion = registry.getSpecFactory(DIALECT_NAME, "2011");
        assertThat(wrongVersion).isInstanceOf(Result.Failure.class);

        Result<PreparedStatementSpecFactory> wrongVersion2 = registry.getSpecFactory(DIALECT_NAME, "2016");
        assertThat(wrongVersion2).isInstanceOf(Result.Failure.class);
    }

    @Test
    void getRendererWithoutVersion() {
        // When version is not specified, should return available plugin
        Result<PreparedStatementSpecFactory> result = registry.getRenderer(DIALECT_NAME);

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldProduceWorkingRenderer() throws SQLException {
        // Get renderer from registry
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION);
        PreparedStatementSpecFactory specFactory = result.orElseThrow();
        DSL dsl = new DSL(specFactory);

        // Execute the query to verify it works with H2
        List<List<Object>> rows = ResultSetUtil.list(
                dsl.select("name", "email").from("users").buildPreparedStatement(connection),
                r -> List.of(r.getString("name"), r.getString("email")));

        assertThat(rows)
                .hasSize(10)
                .extracting(r -> (String) r.get(0), r -> (String) r.get(1))
                .contains(tuple("John Doe", "john@example.com"), tuple("Jane Smith", "jane@example.com"));
    }

    @Test
    void shouldExecuteStandardSQLPagination() throws SQLException {
        // Get renderer from registry
        PreparedStatementSpecFactory specFactory =
                registry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION).orElseThrow();
        DSL dsl = new DSL(specFactory);

        // Execute standard SQL:2008 pagination (OFFSET/FETCH) and verify results
        List<List<Object>> rows = ResultSetUtil.list(
                dsl.select("name")
                        .from("users")
                        .orderBy("name")
                        .offset(5)
                        .fetch(3)
                        .buildPreparedStatement(connection),
                r -> List.of(r.getString("name")));

        // Verify OFFSET 5 and FETCH 3 returned correct users (ordered: Alice, Bob, Charlie, Diana, Eve, Frank, Grace,
        // Henry, Jane Smith, John Doe)
        assertThat(rows).hasSize(3);
        assertThat(rows).extracting(r -> r.get(0)).containsExactly("Frank", "Grace", "Henry");
    }

    @Test
    void shouldUseRendererForDifferentDSLOperations() throws SQLException {
        // Get renderer from registry
        PreparedStatementSpecFactory specFactory =
                registry.getSpecFactory("StandardSQL", "2008").orElseThrow();
        DSL dsl = new DSL(specFactory);

        // Test SELECT with WHERE - execute and verify results
        List<List<Object>> selectResults = ResultSetUtil.list(
                dsl.select("name", "age")
                        .from("users")
                        .where()
                        .column("age")
                        .gt(25)
                        .buildPreparedStatement(connection),
                r -> List.of(r.getString("name"), r.getInt("age")));
        assertThat(selectResults).isNotEmpty();
        assertThat(selectResults).allMatch(row -> ((Integer) row.get(1)) > 25);

        // Test UPDATE - execute and verify affected rows
        int updateCount = dsl.update("users")
                .set("age", 31)
                .where()
                .column("name")
                .eq("John Doe")
                .buildPreparedStatement(connection)
                .executeUpdate();
        assertThat(updateCount).isEqualTo(1);

        // Test DELETE - execute and verify no error (age < 18 may not match any rows)
        int deleteCount = dsl.deleteFrom("users")
                .where()
                .column("age")
                .lt(18)
                .buildPreparedStatement(connection)
                .executeUpdate();
        assertThat(deleteCount).isGreaterThanOrEqualTo(0);

        // Test INSERT - execute and verify insertion
        int insertCount = dsl.insertInto("users")
                .set("id", 123456789)
                .set("name", "Test")
                .set("age", 25)
                .buildPreparedStatement(connection)
                .executeUpdate();
        assertThat(insertCount).isEqualTo(1);

        // Verify the inserted row exists
        List<List<Object>> verifyInsert = ResultSetUtil.list(
                dsl.select("name", "age")
                        .from("users")
                        .where()
                        .column("name")
                        .eq("Test")
                        .buildPreparedStatement(connection),
                r -> List.of(r.getString("name"), r.getInt("age")));
        assertThat(verifyInsert).hasSize(1);
        assertThat(verifyInsert.get(0)).containsExactly("Test", 25);
    }

    @Test
    void shouldBeIntegratedWithMultiplePlugins() {
        // Verify StandardSQL plugin coexists with other plugins (if any)
        assertThat(registry.isEmpty()).isFalse();
        assertThat(registry.size()).isGreaterThanOrEqualTo(1);

        // Verify we can get StandardSQL specifically
        assertThat(registry.getSupportedDialects()).contains("standardsql");
    }

    @Test
    void shouldWorkWithRegistryManualRegistration() {
        // Create a new empty registry and manually register the plugin
        SqlDialectPluginRegistry emptyRegistry = SqlDialectPluginRegistry.empty();
        assertThat(emptyRegistry.isSupported(DIALECT_NAME)).isFalse();

        // Register the plugin
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
        SqlDialectPluginRegistry newRegistry = emptyRegistry.register(plugin);

        // Verify it's now available
        assertThat(newRegistry.isSupported(DIALECT_NAME)).isTrue();
        Result<PreparedStatementSpecFactory> result = newRegistry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION);
        assertThat(result).isInstanceOf(Result.Success.class);
    }

    @Test
    void mergeStatementWithRealDatabase() throws SQLException {
        // Get renderer from registry
        PreparedStatementSpecFactory specFactory =
                registry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION).orElseThrow();
        DSL dsl = new DSL(specFactory);

        // Create source table with user updates
        try (var stmt = connection.createStatement()) {
            stmt.execute(
                    """
                    CREATE TABLE users_updates (
                        "id" INTEGER PRIMARY KEY,
                        "name" VARCHAR(50),
                        "email" VARCHAR(100),
                        "age" INTEGER,
                        "active" BOOLEAN
                    )
                    """);

            // Source has: updated John Doe (age changed), new user (id=11), Jane Smith unchanged
            stmt.execute("INSERT INTO users_updates VALUES (1, 'John Doe', 'john.newemail@example.com', 31, true)");
            stmt.execute("INSERT INTO users_updates VALUES (2, 'Jane Smith', 'jane@example.com', 25, true)");
            stmt.execute("INSERT INTO users_updates VALUES (11, 'New User', 'newuser@example.com', 28, true)");
        }

        // Build and execute MERGE statement using DSL
        int affectedRows = dsl.mergeInto("users")
                .as("tgt")
                .using("users_updates", "src")
                .on("tgt.id", "src.id")
                .whenMatched()
                .set("name", "src.name")
                .set("email", "src.email")
                .set("age", "src.age")
                .set("active", "src.active")
                .whenNotMatched()
                .set("id", "src.id")
                .set("name", "src.name")
                .set("email", "src.email")
                .set("age", "src.age")
                .set("active", "src.active")
                .buildPreparedStatement(connection)
                .executeUpdate();

        assertThat(affectedRows).isGreaterThanOrEqualTo(0);

        // Verify John Doe was updated
        List<List<Object>> johnDoe = ResultSetUtil.list(
                dsl.select("id", "name", "email", "age", "active")
                        .from("users")
                        .where()
                        .column("id")
                        .eq(1)
                        .buildPreparedStatement(connection),
                r -> List.of(
                        r.getInt("id"),
                        r.getString("name"),
                        r.getString("email"),
                        r.getInt("age"),
                        r.getBoolean("active")));

        assertThat(johnDoe).hasSize(1);
        assertThat(johnDoe.get(0)).containsExactly(1, "John Doe", "john.newemail@example.com", 31, true);

        // Verify new user was inserted
        List<List<Object>> newUser = ResultSetUtil.list(
                dsl.select("id", "name", "email", "age", "active")
                        .from("users")
                        .where()
                        .column("id")
                        .eq(11)
                        .buildPreparedStatement(connection),
                r -> List.of(
                        r.getInt("id"),
                        r.getString("name"),
                        r.getString("email"),
                        r.getInt("age"),
                        r.getBoolean("active")));

        assertThat(newUser).hasSize(1);
        assertThat(newUser.get(0)).containsExactly(11, "New User", "newuser@example.com", 28, true);

        // Verify total count (original 10 + 1 new = 11)
        List<Integer> count = ResultSetUtil.list(
                connection.prepareStatement("SELECT COUNT(*) as cnt FROM users"), r -> r.getInt("cnt"));
        assertThat(count.get(0)).isEqualTo(11);
    }
}
