package lan.tlab.r4j.jdsql.plugin.builtin.postgre;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.SQLException;
import lan.tlab.r4j.jdsql.ast.visitor.PreparedStatementSpecFactory;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.jdsql.test.util.annotation.ComponentTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Component tests for PostgreSqlDialectPlugin with SqlDialectRegistry.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader.
 */
@ComponentTest
class PostgreSqlPluginRegistryComponentTest {

    private SqlDialectPluginRegistry pluginRegistry;

    @BeforeEach
    void setUp() throws SQLException {
        pluginRegistry = SqlDialectPluginRegistry.createWithServiceLoader();
    }

    @Test
    void registration() {
        assertThat(pluginRegistry.isEmpty()).isFalse();
        assertThat(pluginRegistry.isSupported(PostgreSqlDialectPlugin.DIALECT_NAME))
                .isTrue();
        assertThat(pluginRegistry.isSupported("postgresql")).isTrue(); // case-insensitive
        assertThat(pluginRegistry.isSupported("POSTGRESQL")).isTrue();
    }

    @Test
    void shouldBeIntegratedWithMultiplePlugins() {
        assertThat(pluginRegistry.size()).isGreaterThanOrEqualTo(2);

        assertThat(pluginRegistry.getSupportedDialects()).contains("postgresql");
    }

    @Test
    void shouldWorkWithRegistryManualRegistration() {
        SqlDialectPluginRegistry emptyRegistry = SqlDialectPluginRegistry.empty();
        assertThat(emptyRegistry.isSupported(PostgreSqlDialectPlugin.DIALECT_NAME))
                .isFalse();

        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();
        SqlDialectPluginRegistry newRegistry = emptyRegistry.register(plugin);

        assertThat(newRegistry.isSupported(PostgreSqlDialectPlugin.DIALECT_NAME))
                .isTrue();
        Result<PreparedStatementSpecFactory> result =
                newRegistry.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, "15.2.0");
        assertThat(result).isInstanceOf(Result.Success.class);
    }

    @Test
    void getRenderer() {
        Result<PreparedStatementSpecFactory> result =
                pluginRegistry.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, "15.2.0");

        assertThat(result).isInstanceOf(Result.Success.class);
        PreparedStatementSpecFactory specFactory = result.orElseThrow();
        assertThat(specFactory).isNotNull();
    }

    @Test
    void versionMatching() {
        // Should match PostgreSQL 15.x versions (using ^15.0.0 range)
        Result<PreparedStatementSpecFactory> version1500 =
                pluginRegistry.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, "15.0.0");
        assertThat(version1500).isInstanceOf(Result.Success.class);

        Result<PreparedStatementSpecFactory> version1520 =
                pluginRegistry.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, "15.2.0");
        assertThat(version1520).isInstanceOf(Result.Success.class);

        Result<PreparedStatementSpecFactory> version1590 =
                pluginRegistry.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, "15.9.0");
        assertThat(version1590).isInstanceOf(Result.Success.class);

        // Should NOT match PostgreSQL 14 or 16
        Result<PreparedStatementSpecFactory> version1400 =
                pluginRegistry.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, "14.10.0");
        assertThat(version1400).isInstanceOf(Result.Failure.class);

        Result<PreparedStatementSpecFactory> version1600 =
                pluginRegistry.getSpecFactory(PostgreSqlDialectPlugin.DIALECT_NAME, "16.0.0");
        assertThat(version1600).isInstanceOf(Result.Failure.class);
    }

    @Test
    void getRendererWithoutVersion() {
        // When version is not specified, should return available plugin
        Result<PreparedStatementSpecFactory> result = pluginRegistry.getRenderer(PostgreSqlDialectPlugin.DIALECT_NAME);

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }
}
