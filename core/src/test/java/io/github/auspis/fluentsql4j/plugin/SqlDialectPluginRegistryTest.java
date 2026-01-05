package io.github.auspis.fluentsql4j.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.functional.Result.Failure;
import io.github.auspis.fluentsql4j.functional.Result.Success;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginRegistry;
import io.github.auspis.fluentsql4j.plugin.util.SqlDialectPluginUtil;
import io.github.auspis.fluentsql4j.plugin.util.TestDialectPlugin;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlDialectPluginRegistryTest {

    private PreparedStatementSpecFactory specFactory;
    private SqlDialectPlugin plugin_3_0_0;
    private SqlDialectPlugin plugin_1_7_0;
    private List<SqlDialectPlugin> plugins;
    private SqlDialectPluginRegistry registry;

    @BeforeEach
    void setUp() {
        specFactory = mock(PreparedStatementSpecFactory.class);
        plugin_3_0_0 = SqlDialectPluginUtil.create(specFactory);
        plugin_1_7_0 = SqlDialectPluginUtil.create("^1.7.0", specFactory);
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
        assertThatThrownBy(() -> SqlDialectPluginUtil.create("", specFactory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining(TestDialectPlugin.DIALECT_NAME);

        assertThatThrownBy(() -> SqlDialectPluginUtil.create("   ", specFactory))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Dialect version must not be blank")
                .hasMessageContaining(TestDialectPlugin.DIALECT_NAME);
    }

    @Test
    void getSpecFactory_returnsSuccessWhenDialectSupported() {
        assertThat(registry.isSupported(TestDialectPlugin.DIALECT_NAME)).isTrue();
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory(TestDialectPlugin.DIALECT_NAME);
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(specFactory);
    }

    @Test
    void getSpecFactory_shouldMatchVersionWithinRange() {
        Result<PreparedStatementSpecFactory> result1 =
                registry.getSpecFactory(TestDialectPlugin.DIALECT_NAME, TestDialectPlugin.DIALECT_VERSION);
        assertThat(result1).isInstanceOf(Success.class);
        assertThat(result1.orElseThrow()).isEqualTo(specFactory);

        Result<PreparedStatementSpecFactory> result2 = registry.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "3.5.0");
        assertThat(result2).isInstanceOf(Success.class);
        assertThat(result2.orElseThrow()).isEqualTo(specFactory);
    }

    @Test
    void getSpecFactory_returnsFailureForVersionOutsideRange() {
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2.7.0");
        assertThat(result).isInstanceOf(Failure.class);
        assertThat(((Failure<PreparedStatementSpecFactory>) result).message()).contains("No plugin found");
    }

    @Test
    void getSpecFactory_returnsFailureForNullDialect() {
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory(null);
        assertThat(result).isInstanceOf(Failure.class);
        assertThat(((Failure<PreparedStatementSpecFactory>) result).message())
                .contains("Dialect name must not be null");
    }

    @Test
    void getSpecFactory_supportsNonSemVerExactMatch() {
        PreparedStatementSpecFactory nonSemVerPreparedStatementSpecFactory = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin nonSemVerPlugin = SqlDialectPluginUtil.create(
                TestDialectPlugin.DIALECT_NAME, "2008", nonSemVerPreparedStatementSpecFactory);
        SqlDialectPluginRegistry registryWithNonSemVer = SqlDialectPluginRegistry.of(List.of(nonSemVerPlugin));

        // Exact match should work
        Result<PreparedStatementSpecFactory> result =
                registryWithNonSemVer.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2008");
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(nonSemVerPreparedStatementSpecFactory);

        // Non-matching version should fail
        Result<PreparedStatementSpecFactory> result2 =
                registryWithNonSemVer.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2011");
        assertThat(result2).isInstanceOf(Failure.class);
    }

    @Test
    void getSpecFactory_supportsMultipleNonSemVerVersions() {
        PreparedStatementSpecFactory specFactory2008 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2011 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2016 = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin2008 =
                SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "2008", specFactory2008);
        SqlDialectPlugin plugin2011 =
                SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "2011", specFactory2011);
        SqlDialectPlugin plugin2016 =
                SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "2016", specFactory2016);

        SqlDialectPluginRegistry registryWithMultipleVersions =
                SqlDialectPluginRegistry.of(List.of(plugin2008, plugin2011, plugin2016));

        // Each version should match its corresponding plugin
        assertThat(registryWithMultipleVersions
                        .getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2008")
                        .orElseThrow())
                .isEqualTo(specFactory2008);
        assertThat(registryWithMultipleVersions
                        .getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2011")
                        .orElseThrow())
                .isEqualTo(specFactory2011);
        assertThat(registryWithMultipleVersions
                        .getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2016")
                        .orElseThrow())
                .isEqualTo(specFactory2016);

        // Non-matching version should fail
        Result<PreparedStatementSpecFactory> result =
                registryWithMultipleVersions.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2019");
        assertThat(result).isInstanceOf(Failure.class);
    }

    @Test
    void getSpecFactory_nonSemVerVersionsCaseSensitive() {
        PreparedStatementSpecFactory specFactoryLower = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin pluginLower =
                SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "v1", specFactoryLower);
        SqlDialectPluginRegistry registryWithVersion = SqlDialectPluginRegistry.of(List.of(pluginLower));

        // Exact case match should work
        Result<PreparedStatementSpecFactory> result =
                registryWithVersion.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "v1");
        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isEqualTo(specFactoryLower);

        // Different case should not match
        Result<PreparedStatementSpecFactory> result2 =
                registryWithVersion.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "V1");
        assertThat(result2).isInstanceOf(Failure.class);
    }

    @Test
    void getSpecFactory_mixedSemVerAndNonSemVerPlugins() {
        PreparedStatementSpecFactory semVerPreparedStatementSpecFactory = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory nonSemVerPreparedStatementSpecFactory = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin semVerPlugin = SqlDialectPluginUtil.create(
                TestDialectPlugin.DIALECT_NAME, "^8.0.0", semVerPreparedStatementSpecFactory);
        SqlDialectPlugin nonSemVerPlugin = SqlDialectPluginUtil.create(
                TestDialectPlugin.DIALECT_NAME, "2008", nonSemVerPreparedStatementSpecFactory);

        SqlDialectPluginRegistry mixedRegistry = SqlDialectPluginRegistry.of(List.of(semVerPlugin, nonSemVerPlugin));

        // SemVer version should match SemVer plugin
        Result<PreparedStatementSpecFactory> semVerResult =
                mixedRegistry.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "8.0.35");
        assertThat(semVerResult).isInstanceOf(Success.class);
        assertThat(semVerResult.orElseThrow()).isEqualTo(semVerPreparedStatementSpecFactory);

        // Non-SemVer version should match non-SemVer plugin
        Result<PreparedStatementSpecFactory> nonSemVerResult =
                mixedRegistry.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "2008");
        assertThat(nonSemVerResult).isInstanceOf(Success.class);
        assertThat(nonSemVerResult.orElseThrow()).isEqualTo(nonSemVerPreparedStatementSpecFactory);
    }

    // Tests for pure function findMatchingPlugins

    @Test
    void findMatchingPlugins_shouldReturnEmptyListForEmptyInput() {
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(
                        Collections.emptyList(), TestDialectPlugin.DIALECT_VERSION))
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
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(plugins, TestDialectPlugin.DIALECT_VERSION))
                .containsExactly(plugin_3_0_0);
    }

    @Test
    void findMatchingPlugins_shouldReturnEmptyWhenNoVersionMatches() {
        assertThat(SqlDialectPluginRegistry.findMatchingPlugins(plugins, "9.0.0"))
                .isEmpty();
    }

    @Test
    void findMatchingPlugins_shouldUseExactMatchForNonSemVer() {
        PreparedStatementSpecFactory specFactory2008 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2011 = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin2008 = SqlDialectPluginUtil.create("2008", specFactory2008);
        SqlDialectPlugin plugin2011 = SqlDialectPluginUtil.create("2011", specFactory2011);

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
        PreparedStatementSpecFactory semVerPreparedStatementSpecFactory = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory nonSemVerPreparedStatementSpecFactory = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin semVerPlugin = SqlDialectPluginUtil.create("^8.0.0", semVerPreparedStatementSpecFactory);
        SqlDialectPlugin nonSemVerPlugin = SqlDialectPluginUtil.create("2008", nonSemVerPreparedStatementSpecFactory);

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
        PreparedStatementSpecFactory newPreparedStatementSpecFactory = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin newPlugin =
                SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^8.0.0", newPreparedStatementSpecFactory);

        SqlDialectPluginRegistry withPlugin = original.register(newPlugin);

        assertThat(original.isEmpty()).isTrue();
        assertThat(original.size()).isZero();
        assertThat(withPlugin.isEmpty()).isFalse();
        assertThat(withPlugin.size()).isEqualTo(1);
    }

    @Test
    void register_allowsChaining() {
        PreparedStatementSpecFactory specFactory1 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory3 = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin1 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^8.0.0", specFactory1);
        SqlDialectPlugin plugin2 =
                SqlDialectPluginUtil.create(TestDialectPlugin.OTHER_DIALECT_NAME, "^14.0.0", specFactory2);
        SqlDialectPlugin plugin3 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^5.7.0", specFactory3);

        SqlDialectPluginRegistry chained = SqlDialectPluginRegistry.empty()
                .register(plugin1)
                .register(plugin2)
                .register(plugin3);

        assertThat(chained.size()).isEqualTo(3);
        assertThat(chained.getSupportedDialects())
                .containsExactlyInAnyOrder(TestDialectPlugin.DIALECT_NAME, TestDialectPlugin.OTHER_DIALECT_NAME);
    }

    @Test
    void register_addsPluginToExistingDialect() {
        PreparedStatementSpecFactory specFactory1 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2 = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin1 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^8.0.0", specFactory1);
        SqlDialectPlugin plugin2 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^5.7.0", specFactory2);

        SqlDialectPluginRegistry withOne = SqlDialectPluginRegistry.empty().register(plugin1);
        assertThat(withOne.size()).isEqualTo(1);

        SqlDialectPluginRegistry withTwo = withOne.register(plugin2);
        assertThat(withTwo.size()).isEqualTo(2);
        assertThat(withTwo.getSupportedDialects()).containsExactly(TestDialectPlugin.DIALECT_NAME);
    }

    @Test
    void register_createsNewDialectEntry() {
        PreparedStatementSpecFactory specFactory1 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2 = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin1 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^8.0.0", specFactory1);
        SqlDialectPlugin plugin2 =
                SqlDialectPluginUtil.create(TestDialectPlugin.OTHER_DIALECT_NAME, "^14.0.0", specFactory2);

        SqlDialectPluginRegistry withFirst = SqlDialectPluginRegistry.empty().register(plugin1);
        assertThat(withFirst.getSupportedDialects()).containsExactly(TestDialectPlugin.DIALECT_NAME);

        SqlDialectPluginRegistry withBoth = withFirst.register(plugin2);
        assertThat(withBoth.getSupportedDialects())
                .containsExactlyInAnyOrder(TestDialectPlugin.DIALECT_NAME, TestDialectPlugin.OTHER_DIALECT_NAME);
    }

    @Test
    void register_preservesInsertionOrder() {
        PreparedStatementSpecFactory specFactory1 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory3 = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin1 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^8.0.0", specFactory1);
        SqlDialectPlugin plugin2 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^5.7.0", specFactory2);
        SqlDialectPlugin plugin3 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^5.6.0", specFactory3);

        SqlDialectPluginRegistry ordered = SqlDialectPluginRegistry.empty()
                .register(plugin1)
                .register(plugin2)
                .register(plugin3);

        Result<PreparedStatementSpecFactory> result = ordered.getSpecFactory(TestDialectPlugin.DIALECT_NAME, "8.0.35");

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isSameAs(specFactory1);
    }

    @Test
    void register_doesNotAffectOtherInstances() {
        PreparedStatementSpecFactory specFactory1 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2 = mock(PreparedStatementSpecFactory.class);

        SqlDialectPlugin plugin1 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^8.0.0", specFactory1);
        SqlDialectPlugin plugin2 =
                SqlDialectPluginUtil.create(TestDialectPlugin.OTHER_DIALECT_NAME, "^14.0.0", specFactory2);

        SqlDialectPluginRegistry registry1 = SqlDialectPluginRegistry.empty().register(plugin1);
        SqlDialectPluginRegistry registry2 = registry1.register(plugin2);

        assertThat(registry1.size()).isEqualTo(1);
        assertThat(registry1.getSupportedDialects()).containsExactly(TestDialectPlugin.DIALECT_NAME);

        assertThat(registry2.size()).isEqualTo(2);
        assertThat(registry2.getSupportedDialects())
                .containsExactlyInAnyOrder(TestDialectPlugin.DIALECT_NAME, TestDialectPlugin.OTHER_DIALECT_NAME);
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
