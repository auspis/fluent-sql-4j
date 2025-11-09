package lan.tlab.r4j.integration.sql.plugin.builtin;

import static lan.tlab.r4j.sql.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_NAME;
import static lan.tlab.r4j.sql.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.util.ResultSetUtil;
import lan.tlab.r4j.sql.functional.Result;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Integration tests for StandardSQLDialectPlugin with SqlDialectRegistry.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader, and produces functional SQL renderers
 * that work with real database operations.
 */
class StandardSQLDialectPluginIntegrationTest {

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
        Result<DialectRenderer> result = registry.getDialectRenderer(DIALECT_NAME, DIALECT_VERSION);

        assertThat(result).isInstanceOf(Result.Success.class);
        DialectRenderer renderer = result.orElseThrow();
        assertThat(renderer).isNotNull();
    }

    @Test
    void versionMatching() {
        Result<DialectRenderer> exactMatch = registry.getDialectRenderer(DIALECT_NAME, DIALECT_VERSION);
        assertThat(exactMatch).isInstanceOf(Result.Success.class);

        Result<DialectRenderer> wrongVersion = registry.getDialectRenderer(DIALECT_NAME, "2011");
        assertThat(wrongVersion).isInstanceOf(Result.Failure.class);

        Result<DialectRenderer> wrongVersion2 = registry.getDialectRenderer(DIALECT_NAME, "2016");
        assertThat(wrongVersion2).isInstanceOf(Result.Failure.class);
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
        Result<DialectRenderer> result = registry.getDialectRenderer(DIALECT_NAME, DIALECT_VERSION);
        DialectRenderer renderer = result.orElseThrow();
        DSL dsl = new DSL(renderer);

        // Verify renderer works with real queries using the DSL
        String sql = dsl.select("name", "email").from("users").build();
        // DSL adds table references and quotes by default
        assertThat(sql).contains("name");
        assertThat(sql).contains("email");
        assertThat(sql).contains("users");

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
    void shouldGenerateStandardSQLSyntax() {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer(DIALECT_NAME, DIALECT_VERSION).orElseThrow();
        DSL dsl = new DSL(renderer);

        // Verify it generates standard SQL:2008 syntax for pagination using the DSL
        String paginationSql = dsl.select("name")
                .from("users")
                .orderBy("name")
                .offset(5)
                .fetch(3)
                .build();

        assertThat(paginationSql).contains("OFFSET 5 ROWS");
        assertThat(paginationSql).contains("FETCH NEXT 3 ROWS ONLY");
    }

    @Test
    void shouldUseRendererForDifferentDSLOperations() throws SQLException {
        // Get renderer from registry
        DialectRenderer renderer =
                registry.getDialectRenderer("StandardSQL", "2008").orElseThrow();
        DSL dsl = new DSL(renderer);
        // Test SELECT with WHERE using the custom renderer
        String selectSql = dsl.select("name", "age")
                .from("users")
                .where()
                .column("age")
                .gt(25)
                .build();
        assertThat(selectSql).contains("WHERE");
        assertThat(selectSql).isNotEmpty();

        // Test UPDATE using the custom renderer
        String updateSql = dsl.update("users")
                .set("age", 31)
                .where()
                .column("name")
                .eq("John Doe")
                .build();
        assertThat(updateSql).contains("UPDATE");
        assertThat(updateSql).contains("SET");

        // Test DELETE using the custom renderer
        String deleteSql = dsl.deleteFrom("users").where().column("age").lt(18).build();
        assertThat(deleteSql).contains("DELETE");
        assertThat(deleteSql).contains("FROM");

        // Test INSERT using the custom renderer
        String insertSql =
                dsl.insertInto("users").set("name", "Test").set("age", 25).build();
        assertThat(insertSql).contains("INSERT");
        assertThat(insertSql).contains("INTO");
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
        Result<DialectRenderer> result = newRegistry.getDialectRenderer(DIALECT_NAME, DIALECT_VERSION);
        assertThat(result).isInstanceOf(Result.Success.class);
    }
}
