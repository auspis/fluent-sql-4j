package lan.tlab.r4j.sql.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlDialectRegistryTest {

    private SqlRenderer renderer;
    private SqlDialectPlugin plugin_3_0_0;
    private SqlDialectPlugin plugin_1_7_0;
    private List<SqlDialectPlugin> plugins;

    @BeforeEach
    void setUp() {
        renderer = mock(SqlRenderer.class);
        plugin_3_0_0 = SqlTestPlugin.create(renderer);
        plugin_1_7_0 = SqlTestPlugin.create("^1.7.0", renderer);
        plugins = List.of(plugin_3_0_0, plugin_1_7_0);
        SqlDialectRegistry.register(plugin_3_0_0);
        SqlDialectRegistry.register(plugin_1_7_0);
    }

    @AfterEach
    void tearDown() {
        SqlDialectRegistry.clear();
    }

    @Test
    void register_shouldThrowExceptionForNullPlugin() {
        assertThatThrownBy(() -> SqlDialectRegistry.register(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Plugin must not be null");
    }

    @Test
    void register_shouldThrowExceptionForInvalidVersionRange() {
        assertThatThrownBy(() -> SqlTestPlugin.create("invalid-range", renderer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version range 'invalid-range'")
                .hasMessageContaining(SqlTestPlugin.TEST_DIALECT);
    }

    @Test
    void getRenderer() {
        assertThat(SqlDialectRegistry.isSupported(SqlTestPlugin.TEST_DIALECT)).isTrue();
        SqlRenderer result = SqlDialectRegistry.getRenderer(SqlTestPlugin.TEST_DIALECT);
        assertThat(result).isEqualTo(renderer);
    }

    @Test
    void getRenderer_shouldMatchVersionWithinRange() {
        assertThat(SqlDialectRegistry.getRenderer(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.BASE_VERSION))
                .isEqualTo(renderer);
        assertThat(SqlDialectRegistry.getRenderer(SqlTestPlugin.TEST_DIALECT, "3.5.0"))
                .isEqualTo(renderer);
    }

    @Test
    void getRenderer_shouldRejectVersionOutsideRange() {
        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer(SqlTestPlugin.TEST_DIALECT, "2.7.0"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No plugin found");
    }

    @Test
    void getRenderer_shouldThrowExceptionForNullDialect() {
        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect name must not be null");
    }

    @Test
    void getRenderer_shouldThrowExceptionForInvalidUserVersionFormat() {
        assertThatThrownBy(() -> SqlDialectRegistry.getRenderer(SqlTestPlugin.TEST_DIALECT, "invalid-version"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version format: 'invalid-version'");
    }

    // Tests for pure function findMatchingPlugins

    @Test
    void findMatchingPlugins_shouldReturnEmptyListForEmptyInput() {
        assertThat(SqlDialectRegistry.findMatchingPlugins(Collections.emptyList(), SqlTestPlugin.BASE_VERSION))
                .isEmpty();
    }

    @Test
    void findMatchingPlugins_shouldReturnAllPluginsWhenVersionIsNull() {
        assertThat(SqlDialectRegistry.findMatchingPlugins(plugins, null)).containsExactlyElementsOf(plugins);
    }

    @Test
    void findMatchingPlugins_shouldReturnAllPluginsWhenVersionIsEmpty() {
        assertThat(SqlDialectRegistry.findMatchingPlugins(plugins, "   ")).containsExactlyElementsOf(plugins);
    }

    @Test
    void findMatchingPlugins_shouldFilterByVersion() {
        assertThat(SqlDialectRegistry.findMatchingPlugins(plugins, SqlTestPlugin.BASE_VERSION))
                .containsExactly(plugin_3_0_0);
    }

    @Test
    void findMatchingPlugins_shouldReturnEmptyWhenNoVersionMatches() {
        assertThat(SqlDialectRegistry.findMatchingPlugins(plugins, "9.0.0")).isEmpty();
    }

    @Test
    void findMatchingPlugins_shouldThrowExceptionForInvalidVersionFormat() {
        assertThatThrownBy(() -> SqlDialectRegistry.findMatchingPlugins(plugins, "invalid-version"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid version format: 'invalid-version'");
    }

    @Test
    void findMatchingPlugins_shouldBeIdempotent() {
        List<SqlDialectPlugin> result1 = SqlDialectRegistry.findMatchingPlugins(plugins, "3.5.0");
        List<SqlDialectPlugin> result2 = SqlDialectRegistry.findMatchingPlugins(plugins, "3.5.0");

        assertThat(result1).isEqualTo(result2);
    }
}
