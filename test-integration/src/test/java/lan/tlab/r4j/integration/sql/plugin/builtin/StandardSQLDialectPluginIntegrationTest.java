package lan.tlab.r4j.integration.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import lan.tlab.r4j.integration.sql.util.TestDatabaseUtil;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.util.ResultSetUtil;
import lan.tlab.r4j.sql.plugin.RegistryResult;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectRegistry;
import lan.tlab.r4j.sql.plugin.builtin.StandardSQLDialectPlugin;
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

    private SqlDialectRegistry registry;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create registry with ServiceLoader to test plugin discovery
        registry = SqlDialectRegistry.createWithServiceLoader();

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
    void shouldBeRegisteredInRegistry() {
        assertThat(registry.isSupported("StandardSQL")).isTrue();
        assertThat(registry.isSupported("standardsql")).isTrue(); // case-insensitive
        assertThat(registry.isSupported("STANDARDSQL")).isTrue();
    }

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        RegistryResult<SqlRenderer> result = registry.getRenderer("StandardSQL", "2008");

        assertThat(result).isInstanceOf(RegistryResult.Success.class);
        SqlRenderer renderer = result.orElseThrow();
        assertThat(renderer).isNotNull();
    }

    @Test
    void shouldMatchExactVersion() {
        // Should match exact version "2008"
        RegistryResult<SqlRenderer> exactMatch = registry.getRenderer("StandardSQL", "2008");
        assertThat(exactMatch).isInstanceOf(RegistryResult.Success.class);

        // Should not match other versions (non-SemVer uses exact matching)
        RegistryResult<SqlRenderer> wrongVersion = registry.getRenderer("StandardSQL", "2011");
        assertThat(wrongVersion).isInstanceOf(RegistryResult.Failure.class);

        RegistryResult<SqlRenderer> wrongVersion2 = registry.getRenderer("StandardSQL", "2016");
        assertThat(wrongVersion2).isInstanceOf(RegistryResult.Failure.class);
    }

    @Test
    void shouldReturnRendererWithoutVersion() {
        // When version is not specified, should return available plugin
        RegistryResult<SqlRenderer> result = registry.getRenderer("StandardSQL");

        assertThat(result).isInstanceOf(RegistryResult.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldProduceWorkingRenderer() throws SQLException {
        // Get renderer from registry
        RegistryResult<SqlRenderer> result = registry.getRenderer("StandardSQL", "2008");
        SqlRenderer renderer = result.orElseThrow();

        // Verify renderer works with real queries using the DSL
        String sql = DSL.select(renderer, "name", "email").from("users").build();
        // DSL adds table references and quotes by default
        assertThat(sql).contains("name");
        assertThat(sql).contains("email");
        assertThat(sql).contains("users");

        // Execute the query to verify it works with H2
        List<List<Object>> rows = ResultSetUtil.list(
                DSL.select(renderer, "name", "email").from("users").buildPreparedStatement(connection),
                r -> List.of(r.getString("name"), r.getString("email")));

        assertThat(rows)
                .hasSize(10)
                .extracting(r -> (String) r.get(0), r -> (String) r.get(1))
                .contains(tuple("John Doe", "john@example.com"), tuple("Jane Smith", "jane@example.com"));
    }

    @Test
    void shouldGenerateStandardSQLSyntax() {
        // Get renderer from registry
        SqlRenderer renderer = registry.getRenderer("StandardSQL", "2008").orElseThrow();

        // Verify it generates standard SQL:2008 syntax for pagination using the DSL
        String paginationSql = DSL.select(renderer, "name")
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
        SqlRenderer renderer = registry.getRenderer("StandardSQL", "2008").orElseThrow();

        // Test SELECT with WHERE using the custom renderer
        String selectSql = DSL.select(renderer, "name", "age")
                .from("users")
                .where("age")
                .gt(25)
                .build();
        assertThat(selectSql).contains("WHERE");
        assertThat(selectSql).isNotEmpty();

        // Test UPDATE using the custom renderer
        String updateSql = DSL.update(renderer, "users")
                .set("age", 31)
                .where("name")
                .eq("John Doe")
                .build();
        assertThat(updateSql).contains("UPDATE");
        assertThat(updateSql).contains("SET");

        // Test DELETE using the custom renderer
        String deleteSql = DSL.deleteFrom(renderer, "users").where("age").lt(18).build();
        assertThat(deleteSql).contains("DELETE");
        assertThat(deleteSql).contains("FROM");

        // Test INSERT using the custom renderer
        String insertSql = DSL.insertInto(renderer, "users")
                .set("name", "Test")
                .set("age", 25)
                .build();
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
        SqlDialectRegistry emptyRegistry = SqlDialectRegistry.empty();
        assertThat(emptyRegistry.isSupported("StandardSQL")).isFalse();

        // Register the plugin
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
        SqlDialectRegistry newRegistry = emptyRegistry.register(plugin);

        // Verify it's now available
        assertThat(newRegistry.isSupported("StandardSQL")).isTrue();
        RegistryResult<SqlRenderer> result = newRegistry.getRenderer("StandardSQL", "2008");
        assertThat(result).isInstanceOf(RegistryResult.Success.class);
    }
}
