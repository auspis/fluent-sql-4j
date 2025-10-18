package lan.tlab.r4j.sql.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.RegistryResult.Failure;
import lan.tlab.r4j.sql.plugin.RegistryResult.Success;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlDialectRegistryTest {

    private SqlRenderer renderer;
    private SqlDialectPlugin plugin_3_0_0;
    private SqlDialectPlugin plugin_1_7_0;
    private List<SqlDialectPlugin> plugins;
    private SqlDialectRegistry registry;

    @BeforeEach
    void setUp() {
        renderer = mock(SqlRenderer.class);
        plugin_3_0_0 = SqlTestPlugin.create(renderer);
        plugin_1_7_0 = SqlTestPlugin.create("^1.7.0", renderer);
        plugins = List.of(plugin_3_0_0, plugin_1_7_0);
        registry = SqlDialectRegistry.of(plugins);
    }

    @Test
    void register_shouldThrowExceptionForNullPlugin() {
        assertThatThrownBy(() -> registry.register(null))
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
    void getRenderer_returnsSuccessWhenDialectSupported() {
        assertThat(registry.isSupported(SqlTestPlugin.TEST_DIALECT)).isTrue();
        RegistryResult<SqlRenderer> result = registry.getRenderer(SqlTestPlugin.TEST_DIALECT);
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(renderer);
    }

    @Test
    void getRenderer_shouldMatchVersionWithinRange() {
        RegistryResult<SqlRenderer> result1 =
                registry.getRenderer(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.BASE_VERSION);
        assertThat(result1).isInstanceOf(Success.class);
        assertThat(result1.orElseThrow()).isEqualTo(renderer);

        RegistryResult<SqlRenderer> result2 = registry.getRenderer(SqlTestPlugin.TEST_DIALECT, "3.5.0");
        assertThat(result2).isInstanceOf(Success.class);
        assertThat(result2.orElseThrow()).isEqualTo(renderer);
    }

    @Test
    void getRenderer_returnsFailureForVersionOutsideRange() {
        RegistryResult<SqlRenderer> result = registry.getRenderer(SqlTestPlugin.TEST_DIALECT, "2.7.0");
        assertThat(result).isInstanceOf(Failure.class);
        assertThat(((Failure<SqlRenderer>) result).message()).contains("No plugin found");
    }

    @Test
    void getRenderer_returnsFailureForNullDialect() {
        RegistryResult<SqlRenderer> result = registry.getRenderer(null);
        assertThat(result).isInstanceOf(Failure.class);
        assertThat(((Failure<SqlRenderer>) result).message()).contains("Dialect name must not be null");
    }

    @Test
    void getRenderer_returnsFailureForInvalidUserVersionFormat() {
        RegistryResult<SqlRenderer> result = registry.getRenderer(SqlTestPlugin.TEST_DIALECT, "invalid-version");
        assertThat(result).isInstanceOf(Failure.class);
        assertThat(((Failure<SqlRenderer>) result).message()).contains("Invalid version format: 'invalid-version'");
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

    // Tests for register() method - immutability and behavior

    @Test
    void register_returnsNewInstanceWithoutModifyingOriginal() {
        SqlDialectRegistry original = SqlDialectRegistry.empty();
        SqlRenderer newRenderer = mock(SqlRenderer.class);
        SqlDialectPlugin newPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", newRenderer);

        SqlDialectRegistry withPlugin = original.register(newPlugin);

        assertThat(original.isEmpty()).isTrue();
        assertThat(original.size()).isZero();
        assertThat(withPlugin.isEmpty()).isFalse();
        assertThat(withPlugin.size()).isEqualTo(1);
    }

    @Test
    void register_allowsChaining() {
        SqlRenderer renderer1 = mock(SqlRenderer.class);
        SqlRenderer renderer2 = mock(SqlRenderer.class);
        SqlRenderer renderer3 = mock(SqlRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.OTHER_DIALECT, "^14.0.0", renderer2);
        SqlDialectPlugin plugin3 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.7.0", renderer3);

        SqlDialectRegistry chained =
                SqlDialectRegistry.empty().register(plugin1).register(plugin2).register(plugin3);

        assertThat(chained.size()).isEqualTo(3);
        assertThat(chained.getSupportedDialects())
                .containsExactlyInAnyOrder(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.OTHER_DIALECT);
    }

    @Test
    void register_addsPluginToExistingDialect() {
        SqlRenderer renderer1 = mock(SqlRenderer.class);
        SqlRenderer renderer2 = mock(SqlRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.7.0", renderer2);

        SqlDialectRegistry withOne = SqlDialectRegistry.empty().register(plugin1);
        assertThat(withOne.size()).isEqualTo(1);

        SqlDialectRegistry withTwo = withOne.register(plugin2);
        assertThat(withTwo.size()).isEqualTo(2);
        assertThat(withTwo.getSupportedDialects()).containsExactly(SqlTestPlugin.TEST_DIALECT);
    }

    @Test
    void register_createsNewDialectEntry() {
        SqlRenderer renderer1 = mock(SqlRenderer.class);
        SqlRenderer renderer2 = mock(SqlRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.OTHER_DIALECT, "^14.0.0", renderer2);

        SqlDialectRegistry withFirst = SqlDialectRegistry.empty().register(plugin1);
        assertThat(withFirst.getSupportedDialects()).containsExactly(SqlTestPlugin.TEST_DIALECT);

        SqlDialectRegistry withBoth = withFirst.register(plugin2);
        assertThat(withBoth.getSupportedDialects())
                .containsExactlyInAnyOrder(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.OTHER_DIALECT);
    }

    @Test
    void register_preservesInsertionOrder() {
        SqlRenderer renderer1 = mock(SqlRenderer.class);
        SqlRenderer renderer2 = mock(SqlRenderer.class);
        SqlRenderer renderer3 = mock(SqlRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.7.0", renderer2);
        SqlDialectPlugin plugin3 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.6.0", renderer3);

        SqlDialectRegistry ordered =
                SqlDialectRegistry.empty().register(plugin1).register(plugin2).register(plugin3);

        RegistryResult<SqlRenderer> result = ordered.getRenderer(SqlTestPlugin.TEST_DIALECT, "8.0.35");

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isSameAs(renderer1);
    }

    @Test
    void register_doesNotAffectOtherInstances() {
        SqlRenderer renderer1 = mock(SqlRenderer.class);
        SqlRenderer renderer2 = mock(SqlRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.OTHER_DIALECT, "^14.0.0", renderer2);

        SqlDialectRegistry registry1 = SqlDialectRegistry.empty().register(plugin1);
        SqlDialectRegistry registry2 = registry1.register(plugin2);

        assertThat(registry1.size()).isEqualTo(1);
        assertThat(registry1.getSupportedDialects()).containsExactly(SqlTestPlugin.TEST_DIALECT);

        assertThat(registry2.size()).isEqualTo(2);
        assertThat(registry2.getSupportedDialects())
                .containsExactlyInAnyOrder(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.OTHER_DIALECT);
    }
}
