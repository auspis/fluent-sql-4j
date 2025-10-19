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
    void getRenderer_supportsNonSemVerExactMatch() {
        SqlRenderer nonSemVerRenderer = mock(SqlRenderer.class);
        SqlDialectPlugin nonSemVerPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2008", nonSemVerRenderer);
        SqlDialectRegistry registryWithNonSemVer = SqlDialectRegistry.of(List.of(nonSemVerPlugin));

        // Exact match should work
        RegistryResult<SqlRenderer> result = registryWithNonSemVer.getRenderer(SqlTestPlugin.TEST_DIALECT, "2008");
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(nonSemVerRenderer);

        // Non-matching version should fail
        RegistryResult<SqlRenderer> result2 = registryWithNonSemVer.getRenderer(SqlTestPlugin.TEST_DIALECT, "2011");
        assertThat(result2).isInstanceOf(Failure.class);
    }

    @Test
    void getRenderer_supportsMultipleNonSemVerVersions() {
        SqlRenderer renderer2008 = mock(SqlRenderer.class);
        SqlRenderer renderer2011 = mock(SqlRenderer.class);
        SqlRenderer renderer2016 = mock(SqlRenderer.class);

        SqlDialectPlugin plugin2008 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2008", renderer2008);
        SqlDialectPlugin plugin2011 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2011", renderer2011);
        SqlDialectPlugin plugin2016 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2016", renderer2016);

        SqlDialectRegistry registryWithMultipleVersions =
                SqlDialectRegistry.of(List.of(plugin2008, plugin2011, plugin2016));

        // Each version should match its corresponding plugin
        assertThat(registryWithMultipleVersions
                        .getRenderer(SqlTestPlugin.TEST_DIALECT, "2008")
                        .orElseThrow())
                .isEqualTo(renderer2008);
        assertThat(registryWithMultipleVersions
                        .getRenderer(SqlTestPlugin.TEST_DIALECT, "2011")
                        .orElseThrow())
                .isEqualTo(renderer2011);
        assertThat(registryWithMultipleVersions
                        .getRenderer(SqlTestPlugin.TEST_DIALECT, "2016")
                        .orElseThrow())
                .isEqualTo(renderer2016);

        // Non-matching version should fail
        RegistryResult<SqlRenderer> result =
                registryWithMultipleVersions.getRenderer(SqlTestPlugin.TEST_DIALECT, "2019");
        assertThat(result).isInstanceOf(Failure.class);
    }

    @Test
    void getRenderer_nonSemVerVersionsCaseSensitive() {
        SqlRenderer rendererLower = mock(SqlRenderer.class);
        SqlDialectPlugin pluginLower = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "v1", rendererLower);
        SqlDialectRegistry registryWithVersion = SqlDialectRegistry.of(List.of(pluginLower));

        // Exact case match should work
        RegistryResult<SqlRenderer> result = registryWithVersion.getRenderer(SqlTestPlugin.TEST_DIALECT, "v1");
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(rendererLower);

        // Different case should not match
        RegistryResult<SqlRenderer> result2 = registryWithVersion.getRenderer(SqlTestPlugin.TEST_DIALECT, "V1");
        assertThat(result2).isInstanceOf(Failure.class);
    }

    @Test
    void getRenderer_mixedSemVerAndNonSemVerPlugins() {
        SqlRenderer semVerRenderer = mock(SqlRenderer.class);
        SqlRenderer nonSemVerRenderer = mock(SqlRenderer.class);

        SqlDialectPlugin semVerPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^8.0.0", semVerRenderer);
        SqlDialectPlugin nonSemVerPlugin = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "2008", nonSemVerRenderer);

        SqlDialectRegistry mixedRegistry = SqlDialectRegistry.of(List.of(semVerPlugin, nonSemVerPlugin));

        // SemVer version should match SemVer plugin
        RegistryResult<SqlRenderer> semVerResult = mixedRegistry.getRenderer(SqlTestPlugin.TEST_DIALECT, "8.0.35");
        assertThat(semVerResult).isInstanceOf(Success.class);
        assertThat(semVerResult.orElseThrow()).isEqualTo(semVerRenderer);

        // Non-SemVer version should match non-SemVer plugin
        RegistryResult<SqlRenderer> nonSemVerResult = mixedRegistry.getRenderer(SqlTestPlugin.TEST_DIALECT, "2008");
        assertThat(nonSemVerResult).isInstanceOf(Success.class);
        assertThat(nonSemVerResult.orElseThrow()).isEqualTo(nonSemVerRenderer);
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
    void findMatchingPlugins_shouldUseExactMatchForNonSemVer() {
        SqlRenderer renderer2008 = mock(SqlRenderer.class);
        SqlRenderer renderer2011 = mock(SqlRenderer.class);

        SqlDialectPlugin plugin2008 = SqlTestPlugin.create("2008", renderer2008);
        SqlDialectPlugin plugin2011 = SqlTestPlugin.create("2011", renderer2011);

        List<SqlDialectPlugin> nonSemVerPlugins = List.of(plugin2008, plugin2011);

        // Exact match should work
        assertThat(SqlDialectRegistry.findMatchingPlugins(nonSemVerPlugins, "2008"))
                .containsExactly(plugin2008);
        assertThat(SqlDialectRegistry.findMatchingPlugins(nonSemVerPlugins, "2011"))
                .containsExactly(plugin2011);

        // Non-matching version should return empty
        assertThat(SqlDialectRegistry.findMatchingPlugins(nonSemVerPlugins, "2016"))
                .isEmpty();
    }

    @Test
    void findMatchingPlugins_shouldHandleMixedSemVerAndNonSemVer() {
        SqlRenderer semVerRenderer = mock(SqlRenderer.class);
        SqlRenderer nonSemVerRenderer = mock(SqlRenderer.class);

        SqlDialectPlugin semVerPlugin = SqlTestPlugin.create("^8.0.0", semVerRenderer);
        SqlDialectPlugin nonSemVerPlugin = SqlTestPlugin.create("2008", nonSemVerRenderer);

        List<SqlDialectPlugin> mixedPlugins = List.of(semVerPlugin, nonSemVerPlugin);

        // SemVer version should match SemVer plugin only
        assertThat(SqlDialectRegistry.findMatchingPlugins(mixedPlugins, "8.0.35"))
                .containsExactly(semVerPlugin);

        // Non-SemVer version should match non-SemVer plugin only
        assertThat(SqlDialectRegistry.findMatchingPlugins(mixedPlugins, "2008")).containsExactly(nonSemVerPlugin);
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

    // Tests for matchesVersion static method

    @Test
    void matchesVersion_shouldMatchSemVerWithSemVerRange() {
        // SemVer version with SemVer range
        assertThat(SqlDialectRegistry.matchesVersion("8.0.35", "^8.0.0", true)).isTrue();
        assertThat(SqlDialectRegistry.matchesVersion("8.5.0", "^8.0.0", true)).isTrue();
        assertThat(SqlDialectRegistry.matchesVersion("9.0.0", "^8.0.0", true)).isFalse();
    }

    @Test
    void matchesVersion_shouldMatchSemVerWithExactVersion() {
        // SemVer exact match
        assertThat(SqlDialectRegistry.matchesVersion("8.0.35", "8.0.35", true)).isTrue();
        assertThat(SqlDialectRegistry.matchesVersion("8.0.35", "8.0.36", true)).isFalse();
    }

    @Test
    void matchesVersion_shouldMatchNonSemVerWithExactString() {
        // Non-SemVer version with exact string match
        assertThat(SqlDialectRegistry.matchesVersion("2008", "2008", false)).isTrue();
        assertThat(SqlDialectRegistry.matchesVersion("2008", "2011", false)).isFalse();
        assertThat(SqlDialectRegistry.matchesVersion("v1", "v1", false)).isTrue();
        assertThat(SqlDialectRegistry.matchesVersion("v1", "V1", false)).isFalse();
    }

    @Test
    void matchesVersion_shouldHandleNonSemVerRequestWithSemVerPlugin() {
        // Non-SemVer request version with SemVer plugin should use exact match
        assertThat(SqlDialectRegistry.matchesVersion("2008", "^8.0.0", false)).isFalse();
        assertThat(SqlDialectRegistry.matchesVersion("latest", "^8.0.0", false)).isFalse();
    }

    @Test
    void matchesVersion_shouldHandleSemVerRequestWithNonSemVerPlugin() {
        // SemVer request version with non-SemVer plugin - will try SemVer match and fail
        assertThat(SqlDialectRegistry.matchesVersion("8.0.35", "2008", true)).isFalse();
    }
}
