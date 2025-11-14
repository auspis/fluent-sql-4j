package lan.tlab.r4j.sql.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;
import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.functional.Result;
import lan.tlab.r4j.sql.functional.Result.Failure;
import lan.tlab.r4j.sql.functional.Result.Success;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlDialectPluginRegistryTest {

    private DialectRenderer renderer;
    private SqlDialectPlugin plugin_3_0_0;
    private SqlDialectPlugin plugin_1_7_0;
    private List<SqlDialectPlugin> plugins;
    private SqlDialectPluginRegistry registry;

    @BeforeEach
    void setUp() {
        renderer = mock(DialectRenderer.class);
        plugin_3_0_0 = SqlTestPlugin.create(renderer);
        plugin_1_7_0 = SqlTestPlugin.create("^1.7.0", renderer);
        plugins = List.of(plugin_3_0_0, plugin_1_7_0);
        registry = SqlDialectPluginRegistry.of(plugins);
    }

    @Test
    void register_shouldThrowExceptionForNullPlugin() {
        assertThatThrownBy(() -> registry.register(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Plugin must not be null");
    }

    @Test
    void register_shouldThrowExceptionForBlankVersion() {
        assertThatThrownBy(() -> SqlTestPlugin.create("", renderer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining(SqlTestPlugin.TEST_DIALECT);

        assertThatThrownBy(() -> SqlTestPlugin.create("   ", renderer))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining(SqlTestPlugin.TEST_DIALECT);
    }

    @Test
    void getRenderer_returnsSuccessWhenDialectSupported() {
        assertThat(registry.isSupported(SqlTestPlugin.TEST_DIALECT)).isTrue();
        Result<DialectRenderer> result = registry.getRenderer(SqlTestPlugin.TEST_DIALECT);
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(renderer);
    }

    @Test
    void getRenderer_shouldMatchVersionWithinRange() {
        Result<DialectRenderer> result1 =
                registry.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.BASE_VERSION);
        assertThat(result1).isInstanceOf(Success.class);
        assertThat(result1.orElseThrow()).isEqualTo(renderer);

        Result<DialectRenderer> result2 = registry.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "3.5.0");
        assertThat(result2).isInstanceOf(Success.class);
        assertThat(result2.orElseThrow()).isEqualTo(renderer);
    }

    @Test
    void getRenderer_returnsFailureForVersionOutsideRange() {
        Result<DialectRenderer> result = registry.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2.7.0");
        assertThat(result).isInstanceOf(Failure.class);
        assertThat(((Failure<DialectRenderer>) result).message()).contains("No plugin found");
    }

    @Test
    void getRenderer_returnsFailureForNullDialect() {
        Result<DialectRenderer> result = registry.getRenderer(null);
        assertThat(result).isInstanceOf(Failure.class);
        assertThat(((Failure<DialectRenderer>) result).message()).contains("Dialect name must not be null");
    }

    @Test
    void getRenderer_supportsNonSemVerExactMatch() {
        DialectRenderer nonSemVerRenderer = mock(DialectRenderer.class);
        SqlDialectPlugin nonSemVerPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2008", nonSemVerRenderer);
        SqlDialectPluginRegistry registryWithNonSemVer = SqlDialectPluginRegistry.of(List.of(nonSemVerPlugin));

        // Exact match should work
        Result<DialectRenderer> result = registryWithNonSemVer.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2008");
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(nonSemVerRenderer);

        // Non-matching version should fail
        Result<DialectRenderer> result2 = registryWithNonSemVer.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2011");
        assertThat(result2).isInstanceOf(Failure.class);
    }

    @Test
    void getRenderer_supportsMultipleNonSemVerVersions() {
        DialectRenderer renderer2008 = mock(DialectRenderer.class);
        DialectRenderer renderer2011 = mock(DialectRenderer.class);
        DialectRenderer renderer2016 = mock(DialectRenderer.class);

        SqlDialectPlugin plugin2008 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2008", renderer2008);
        SqlDialectPlugin plugin2011 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2011", renderer2011);
        SqlDialectPlugin plugin2016 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2016", renderer2016);

        SqlDialectPluginRegistry registryWithMultipleVersions =
                SqlDialectPluginRegistry.of(List.of(plugin2008, plugin2011, plugin2016));

        // Each version should match its corresponding plugin
        assertThat(registryWithMultipleVersions
                        .getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2008")
                        .orElseThrow())
                .isEqualTo(renderer2008);
        assertThat(registryWithMultipleVersions
                        .getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2011")
                        .orElseThrow())
                .isEqualTo(renderer2011);
        assertThat(registryWithMultipleVersions
                        .getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2016")
                        .orElseThrow())
                .isEqualTo(renderer2016);

        // Non-matching version should fail
        Result<DialectRenderer> result =
                registryWithMultipleVersions.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2019");
        assertThat(result).isInstanceOf(Failure.class);
    }

    @Test
    void getRenderer_nonSemVerVersionsCaseSensitive() {
        DialectRenderer rendererLower = mock(DialectRenderer.class);
        SqlDialectPlugin pluginLower = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "v1", rendererLower);
        SqlDialectPluginRegistry registryWithVersion = SqlDialectPluginRegistry.of(List.of(pluginLower));

        // Exact case match should work
        Result<DialectRenderer> result = registryWithVersion.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "v1");
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(rendererLower);

        // Different case should not match
        Result<DialectRenderer> result2 = registryWithVersion.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "V1");
        assertThat(result2).isInstanceOf(Failure.class);
    }

    @Test
    void getRenderer_mixedSemVerAndNonSemVerPlugins() {
        DialectRenderer semVerRenderer = mock(DialectRenderer.class);
        DialectRenderer nonSemVerRenderer = mock(DialectRenderer.class);

        SqlDialectPlugin semVerPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", semVerRenderer);
        SqlDialectPlugin nonSemVerPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2008", nonSemVerRenderer);

        SqlDialectPluginRegistry mixedRegistry = SqlDialectPluginRegistry.of(List.of(semVerPlugin, nonSemVerPlugin));

        // SemVer version should match SemVer plugin
        Result<DialectRenderer> semVerResult = mixedRegistry.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "8.0.35");
        assertThat(semVerResult).isInstanceOf(Success.class);
        assertThat(semVerResult.orElseThrow()).isEqualTo(semVerRenderer);

        // Non-SemVer version should match non-SemVer plugin
        Result<DialectRenderer> nonSemVerResult = mixedRegistry.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "2008");
        assertThat(nonSemVerResult).isInstanceOf(Success.class);
        assertThat(nonSemVerResult.orElseThrow()).isEqualTo(nonSemVerRenderer);
    }

    // Tests for pure function findMatchingPlugins

    @Test
    void findMatchingPlugins_shouldReturnEmptyListForEmptyInput() {
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(Collections.emptyList(), SqlTestPlugin.BASE_VERSION))
                .isEmpty();
    }

    @Test
    void findMatchingPlugins_shouldReturnAllPluginsWhenVersionIsNull() {
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(plugins, null)).containsExactlyElementsOf(plugins);
    }

    @Test
    void findMatchingPlugins_shouldReturnAllPluginsWhenVersionIsEmpty() {
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(plugins, "   ")).containsExactlyElementsOf(plugins);
    }

    @Test
    void findMatchingPlugins_shouldFilterByVersion() {
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(plugins, SqlTestPlugin.BASE_VERSION))
                .containsExactly(plugin_3_0_0);
    }

    @Test
    void findMatchingPlugins_shouldReturnEmptyWhenNoVersionMatches() {
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(plugins, "9.0.0"))
                .isEmpty();
    }

    @Test
    void findMatchingPlugins_shouldUseExactMatchForNonSemVer() {
        DialectRenderer renderer2008 = mock(DialectRenderer.class);
        DialectRenderer renderer2011 = mock(DialectRenderer.class);

        SqlDialectPlugin plugin2008 = SqlTestPlugin.create("2008", renderer2008);
        SqlDialectPlugin plugin2011 = SqlTestPlugin.create("2011", renderer2011);

        List<SqlDialectPlugin> nonSemVerPlugins = List.of(plugin2008, plugin2011);

        // Exact match should work
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(nonSemVerPlugins, "2008"))
                .containsExactly(plugin2008);
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(nonSemVerPlugins, "2011"))
                .containsExactly(plugin2011);

        // Non-matching version should return empty
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(nonSemVerPlugins, "2016"))
                .isEmpty();
    }

    @Test
    void findMatchingPlugins_shouldHandleMixedSemVerAndNonSemVer() {
        DialectRenderer semVerRenderer = mock(DialectRenderer.class);
        DialectRenderer nonSemVerRenderer = mock(DialectRenderer.class);

        SqlDialectPlugin semVerPlugin = SqlTestPlugin.create("^8.0.0", semVerRenderer);
        SqlDialectPlugin nonSemVerPlugin = SqlTestPlugin.create("2008", nonSemVerRenderer);

        List<SqlDialectPlugin> mixedPlugins = List.of(semVerPlugin, nonSemVerPlugin);

        // SemVer version should match SemVer plugin only
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(mixedPlugins, "8.0.35"))
                .containsExactly(semVerPlugin);

        // Non-SemVer version should match non-SemVer plugin only
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(mixedPlugins, "2008"))
                .containsExactly(nonSemVerPlugin);
    }

    @Test
    void findMatchingPlugins_shouldBeIdempotent() {
        List<SqlDialectPlugin> result1 = SqlDialectPluginRegistry.findMatchingPlugins(plugins, "3.5.0");
        List<SqlDialectPlugin> result2 = SqlDialectPluginRegistry.findMatchingPlugins(plugins, "3.5.0");

        assertThat(result1).isEqualTo(result2);
    }

    // Tests for register() method - immutability and behavior

    @Test
    void register_returnsNewInstanceWithoutModifyingOriginal() {
        SqlDialectPluginRegistry original = SqlDialectPluginRegistry.empty();
        DialectRenderer newRenderer = mock(DialectRenderer.class);
        SqlDialectPlugin newPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", newRenderer);

        SqlDialectPluginRegistry withPlugin = original.register(newPlugin);

        assertThat(original.isEmpty()).isTrue();
        assertThat(original.size()).isZero();
        assertThat(withPlugin.isEmpty()).isFalse();
        assertThat(withPlugin.size()).isEqualTo(1);
    }

    @Test
    void register_allowsChaining() {
        DialectRenderer renderer1 = mock(DialectRenderer.class);
        DialectRenderer renderer2 = mock(DialectRenderer.class);
        DialectRenderer renderer3 = mock(DialectRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.OTHER_DIALECT, "^14.0.0", renderer2);
        SqlDialectPlugin plugin3 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.7.0", renderer3);

        SqlDialectPluginRegistry chained = SqlDialectPluginRegistry.empty()
                .register(plugin1)
                .register(plugin2)
                .register(plugin3);

        assertThat(chained.size()).isEqualTo(3);
        assertThat(chained.getSupportedDialects())
                .containsExactlyInAnyOrder(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.OTHER_DIALECT);
    }

    @Test
    void register_addsPluginToExistingDialect() {
        DialectRenderer renderer1 = mock(DialectRenderer.class);
        DialectRenderer renderer2 = mock(DialectRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.7.0", renderer2);

        SqlDialectPluginRegistry withOne = SqlDialectPluginRegistry.empty().register(plugin1);
        assertThat(withOne.size()).isEqualTo(1);

        SqlDialectPluginRegistry withTwo = withOne.register(plugin2);
        assertThat(withTwo.size()).isEqualTo(2);
        assertThat(withTwo.getSupportedDialects()).containsExactly(SqlTestPlugin.TEST_DIALECT);
    }

    @Test
    void register_createsNewDialectEntry() {
        DialectRenderer renderer1 = mock(DialectRenderer.class);
        DialectRenderer renderer2 = mock(DialectRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.OTHER_DIALECT, "^14.0.0", renderer2);

        SqlDialectPluginRegistry withFirst = SqlDialectPluginRegistry.empty().register(plugin1);
        assertThat(withFirst.getSupportedDialects()).containsExactly(SqlTestPlugin.TEST_DIALECT);

        SqlDialectPluginRegistry withBoth = withFirst.register(plugin2);
        assertThat(withBoth.getSupportedDialects())
                .containsExactlyInAnyOrder(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.OTHER_DIALECT);
    }

    @Test
    void register_preservesInsertionOrder() {
        DialectRenderer renderer1 = mock(DialectRenderer.class);
        DialectRenderer renderer2 = mock(DialectRenderer.class);
        DialectRenderer renderer3 = mock(DialectRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.7.0", renderer2);
        SqlDialectPlugin plugin3 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^5.6.0", renderer3);

        SqlDialectPluginRegistry ordered = SqlDialectPluginRegistry.empty()
                .register(plugin1)
                .register(plugin2)
                .register(plugin3);

        Result<DialectRenderer> result = ordered.getDialectRenderer(SqlTestPlugin.TEST_DIALECT, "8.0.35");

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isSameAs(renderer1);
    }

    @Test
    void register_doesNotAffectOtherInstances() {
        DialectRenderer renderer1 = mock(DialectRenderer.class);
        DialectRenderer renderer2 = mock(DialectRenderer.class);

        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.OTHER_DIALECT, "^14.0.0", renderer2);

        SqlDialectPluginRegistry registry1 = SqlDialectPluginRegistry.empty().register(plugin1);
        SqlDialectPluginRegistry registry2 = registry1.register(plugin2);

        assertThat(registry1.size()).isEqualTo(1);
        assertThat(registry1.getSupportedDialects()).containsExactly(SqlTestPlugin.TEST_DIALECT);

        assertThat(registry2.size()).isEqualTo(2);
        assertThat(registry2.getSupportedDialects())
                .containsExactlyInAnyOrder(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.OTHER_DIALECT);
    }

    // Tests for matchesVersion static method

    @Test
    void matchesVersion_shouldMatchSemVerWithSemVerRange() {
        // SemVer version with SemVer range
        assertThat(SqlDialectPluginRegistry.matchesVersion("8.0.35", "^8.0.0", true))
                .isTrue();
        assertThat(SqlDialectPluginRegistry.matchesVersion("8.5.0", "^8.0.0", true))
                .isTrue();
        assertThat(SqlDialectPluginRegistry.matchesVersion("9.0.0", "^8.0.0", true))
                .isFalse();
    }

    @Test
    void matchesVersion_shouldMatchSemVerWithExactVersion() {
        // SemVer exact match
        assertThat(SqlDialectPluginRegistry.matchesVersion("8.0.35", "8.0.35", true))
                .isTrue();
        assertThat(SqlDialectPluginRegistry.matchesVersion("8.0.35", "8.0.36", true))
                .isFalse();
    }

    @Test
    void matchesVersion_shouldMatchNonSemVerWithExactString() {
        // Non-SemVer version with exact string match
        assertThat(SqlDialectPluginRegistry.matchesVersion("2008", "2008", false))
                .isTrue();
        assertThat(SqlDialectPluginRegistry.matchesVersion("2008", "2011", false))
                .isFalse();
        assertThat(SqlDialectPluginRegistry.matchesVersion("v1", "v1", false)).isTrue();
        assertThat(SqlDialectPluginRegistry.matchesVersion("v1", "V1", false)).isFalse();
    }

    @Test
    void matchesVersion_shouldHandleNonSemVerRequestWithSemVerPlugin() {
        // Non-SemVer request version with SemVer plugin should use exact match
        assertThat(SqlDialectPluginRegistry.matchesVersion("2008", "^8.0.0", false))
                .isFalse();
        assertThat(SqlDialectPluginRegistry.matchesVersion("latest", "^8.0.0", false))
                .isFalse();
    }

    @Test
    void matchesVersion_shouldHandleSemVerRequestWithNonSemVerPlugin() {
        // SemVer request version with non-SemVer plugin - will try SemVer match and fail
        assertThat(SqlDialectPluginRegistry.matchesVersion("8.0.35", "2008", true))
                .isFalse();
    }
}
