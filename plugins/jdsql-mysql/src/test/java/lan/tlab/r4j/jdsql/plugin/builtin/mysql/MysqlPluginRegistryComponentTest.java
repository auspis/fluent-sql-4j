package lan.tlab.r4j.jdsql.plugin.builtin.mysql;

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
 * Component tests for MySQLDialectPlugin with SqlDialectRegistry.
 * <p>
 * These tests verify that the plugin integrates correctly with the registry,
 * is discoverable via ServiceLoader.
 */
@ComponentTest
class MysqlPluginRegistryComponentTest {

    private SqlDialectPluginRegistry pluginRegistry;

    @BeforeEach
    void setUp() throws SQLException {
        pluginRegistry = SqlDialectPluginRegistry.createWithServiceLoader();
    }

    @Test
    void registration() {
        assertThat(pluginRegistry.isEmpty()).isFalse();
        assertThat(pluginRegistry.isSupported(MysqlDialectPlugin.DIALECT_NAME)).isTrue();
        assertThat(pluginRegistry.isSupported("mysql")).isTrue(); // case-insensitive
        assertThat(pluginRegistry.isSupported("MYSQL")).isTrue();
    }

    @Test
    void shouldBeIntegratedWithMultiplePlugins() {
        assertThat(pluginRegistry.size()).isGreaterThanOrEqualTo(2);

        assertThat(pluginRegistry.getSupportedDialects()).contains("mysql");
    }

    @Test
    void shouldWorkWithRegistryManualRegistration() {
        SqlDialectPluginRegistry emptyRegistry = SqlDialectPluginRegistry.empty();
        assertThat(emptyRegistry.isSupported(MysqlDialectPlugin.DIALECT_NAME)).isFalse();

        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();
        SqlDialectPluginRegistry newRegistry = emptyRegistry.register(plugin);

        assertThat(newRegistry.isSupported(MysqlDialectPlugin.DIALECT_NAME)).isTrue();
        Result<PreparedStatementSpecFactory> result =
                newRegistry.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, "8.0.35");
        assertThat(result).isInstanceOf(Result.Success.class);
    }

    @Test
    void getRenderer() {
        Result<PreparedStatementSpecFactory> result =
                pluginRegistry.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, "8.0.35");

        assertThat(result).isInstanceOf(Result.Success.class);
        PreparedStatementSpecFactory specFactory = result.orElseThrow();
        assertThat(specFactory).isNotNull();
    }

    @Test
    void versionMatching() {
        // Should match MySQL 8.x versions (using ^8.0.0 range)
        Result<PreparedStatementSpecFactory> version800 =
                pluginRegistry.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, "8.0.0");
        assertThat(version800).isInstanceOf(Result.Success.class);

        Result<PreparedStatementSpecFactory> version8035 =
                pluginRegistry.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, "8.0.35");
        assertThat(version8035).isInstanceOf(Result.Success.class);

        Result<PreparedStatementSpecFactory> version810 =
                pluginRegistry.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, "8.1.0");
        assertThat(version810).isInstanceOf(Result.Success.class);

        // Should NOT match MySQL 5.7 or 9.0
        Result<PreparedStatementSpecFactory> version57 =
                pluginRegistry.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, "5.7.42");
        assertThat(version57).isInstanceOf(Result.Failure.class);

        Result<PreparedStatementSpecFactory> version90 =
                pluginRegistry.getSpecFactory(MysqlDialectPlugin.DIALECT_NAME, "9.0.0");
        assertThat(version90).isInstanceOf(Result.Failure.class);
    }

    @Test
    void getRendererWithoutVersion() {
        // When version is not specified, should return available plugin
        Result<PreparedStatementSpecFactory> result = pluginRegistry.getRenderer(MysqlDialectPlugin.DIALECT_NAME);

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }
}
