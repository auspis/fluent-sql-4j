package integration.plugin;

import static io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_NAME;
import static io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin.DIALECT_VERSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.dsl.util.ResultSetUtil;
import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginRegistry;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import io.github.auspis.fluentsql4j.test.util.TestDatabaseUtil;
import io.github.auspis.fluentsql4j.test.util.annotation.IntegrationTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Integration tests for StandardSQLDialectPlugin with SqlDialectRegistry and H2 database.
 * Tests the complete integration between standard SQL plugin, registry system,
 * ServiceLoader discovery, SQL rendering, and database operations.
 */
@IntegrationTest
class StandardSQLDialectPluginIntegrationTest {

    private SqlDialectPluginRegistry registry;
    private Connection connection;

    @BeforeEach
    void setUp() throws SQLException {
        // Create registry with ServiceLoader to test plugin discovery
        registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Set up database for specFactory functionality tests
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
        // Get specFactory from registry
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
    void shouldGenerateStandardSQLSyntax() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(mockConnection.prepareStatement(sqlCaptor.capture())).thenReturn(mockPs);

        PreparedStatementSpecFactory specFactory =
                registry.getSpecFactory(DIALECT_NAME, DIALECT_VERSION).orElseThrow();
        DSL dsl = new DSL(specFactory);

        // Verify it generates standard SQL:2008 syntax for pagination using the DSL
        dsl.select("name")
                .from("users")
                .orderBy()
                .asc("name")
                .offset(5)
                .fetch(3)
                .build(mockConnection);

        assertThat(sqlCaptor.getValue()).contains("OFFSET 5 ROWS");
        assertThat(sqlCaptor.getValue()).contains("FETCH NEXT 3 ROWS ONLY");
    }

    @Test
    void shouldUsePreparedStatementSpecFactoryForDifferentDSLOperations() throws SQLException {
        Connection mockConnection = mock(Connection.class);
        PreparedStatement mockPs = mock(PreparedStatement.class);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(mockConnection.prepareStatement(sqlCaptor.capture())).thenReturn(mockPs);

        PreparedStatementSpecFactory specFactory =
                registry.getSpecFactory("StandardSQL", "2008").orElseThrow();
        DSL dsl = new DSL(specFactory);

        // Test SELECT with WHERE using the custom renderer
        dsl.select("name", "age").from("users").where().column("age").gt(25).build(mockConnection);
        assertThat(sqlCaptor.getValue()).contains("WHERE");
        assertThat(sqlCaptor.getValue()).isNotEmpty();
        verify(mockPs).setObject(1, 25);

        // Test UPDATE using the custom renderer
        dsl.update("users").set("age", 31).where().column("name").eq("John Doe").build(mockConnection);
        assertThat(sqlCaptor.getValue()).contains("UPDATE");
        assertThat(sqlCaptor.getValue()).contains("SET");
        verify(mockPs).setObject(1, 31);
        verify(mockPs).setObject(2, "John Doe");

        // Test DELETE using the custom renderer
        dsl.deleteFrom("users").where().column("age").lt(18).build(mockConnection);
        assertThat(sqlCaptor.getValue()).contains("DELETE");
        assertThat(sqlCaptor.getValue()).contains("FROM");
        verify(mockPs).setObject(1, 18);

        // Test INSERT using the custom renderer
        dsl.insertInto("users").set("name", "Test").set("age", 25).build(mockConnection);
        assertThat(sqlCaptor.getValue()).contains("INSERT");
        assertThat(sqlCaptor.getValue()).contains("INTO");
        verify(mockPs).setObject(1, "Test");
        verify(mockPs).setObject(2, 25);
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
}
