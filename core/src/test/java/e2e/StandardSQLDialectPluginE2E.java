package e2e;

import static io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_NAME;
import static io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.dsl.util.ResultSetUtil;
import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginRegistry;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import io.github.auspis.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.auspis.fluentsql4j.test.util.annotation.E2ETest;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
    void getSpecFactory() {
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
    void getSpecFactoryWithoutVersion() {
        // When version is not specified, should return available plugin
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory(DIALECT_NAME);

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldProduceWorkingPreparedStatementSpecFactory() throws SQLException {
        // Get renderer from registry
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION);
        PreparedStatementSpecFactory specFactory = result.orElseThrow();
        DSL dsl = new DSL(specFactory);

        // Execute the query to verify it works with H2
        List<List<Object>> rows = ResultSetUtil.list(
                dsl.select("name", "email").from("users").build(connection),
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
                        .orderBy()
                        .asc("name")
                        .offset(5)
                        .fetch(3)
                        .build(connection),
                r -> List.of(r.getString("name")));

        // Verify OFFSET 5 and FETCH 3 returned correct users (ordered: Alice, Bob, Charlie, Diana, Eve, Frank, Grace,
        // Henry, Jane Smith, John Doe)
        assertThat(rows).hasSize(3);
        assertThat(rows).extracting(r -> r.get(0)).containsExactly("Frank", "Grace", "Henry");
    }

    @Test
    void shouldUsePreparedStatementSpecFactoryForDifferentDSLOperations() throws SQLException {
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
                        .build(connection),
                r -> List.of(r.getString("name"), r.getInt("age")));
        assertThat(selectResults).isNotEmpty();
        assertThat(selectResults).allMatch(row -> ((Integer) row.get(1)) > 25);

        // Test UPDATE - execute and verify affected rows
        int updateCount = dsl.update("users")
                .set("age", 31)
                .where()
                .column("name")
                .eq("John Doe")
                .build(connection)
                .executeUpdate();
        assertThat(updateCount).isEqualTo(1);

        // Test DELETE - execute and verify no error (age < 18 may not match any rows)
        int deleteCount = dsl.deleteFrom("users")
                .where()
                .column("age")
                .lt(18)
                .build(connection)
                .executeUpdate();
        assertThat(deleteCount).isGreaterThanOrEqualTo(0);

        // Test INSERT - execute and verify insertion
        int insertCount = dsl.insertInto("users")
                .set("id", 123456789)
                .set("name", "Test")
                .set("age", 25)
                .build(connection)
                .executeUpdate();
        assertThat(insertCount).isEqualTo(1);

        // Verify the inserted row exists
        List<List<Object>> verifyInsert = ResultSetUtil.list(
                dsl.select("name", "age")
                        .from("users")
                        .where()
                        .column("name")
                        .eq("Test")
                        .build(connection),
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
            stmt.execute("""
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
                .on("tgt", "id", "src", "id")
                .whenMatched()
                .set("name", ColumnReference.of("src", "name"))
                .set("email", ColumnReference.of("src", "email"))
                .set("age", ColumnReference.of("src", "age"))
                .set("active", ColumnReference.of("src", "active"))
                .whenNotMatched()
                .set("id", ColumnReference.of("src", "id"))
                .set("name", ColumnReference.of("src", "name"))
                .set("email", ColumnReference.of("src", "email"))
                .set("age", ColumnReference.of("src", "age"))
                .set("active", ColumnReference.of("src", "active"))
                .build(connection)
                .executeUpdate();

        assertThat(affectedRows).isGreaterThanOrEqualTo(0);

        // Verify John Doe was updated
        List<List<Object>> johnDoe = ResultSetUtil.list(
                dsl.select("id", "name", "email", "age", "active")
                        .from("users")
                        .where()
                        .column("id")
                        .eq(1)
                        .build(connection),
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
                        .build(connection),
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
