package lan.tlab.r4j.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.ServiceLoader;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.RegistryResult;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;
import lan.tlab.r4j.sql.plugin.SqlDialectRegistry;
import org.junit.jupiter.api.Test;

class StandardSQLDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> loader = ServiceLoader.load(SqlDialectPluginProvider.class);

        List<SqlDialectPlugin> plugins = loader.stream()
                .map(ServiceLoader.Provider::get)
                .map(SqlDialectPluginProvider::get)
                .toList();

        // Should find at least the StandardSQLDialectPlugin
        assertThat(plugins).isNotEmpty();

        // Verify StandardSQLDialectPlugin is among discovered plugins
        boolean foundStandardSQL = plugins.stream()
                .anyMatch(p -> "StandardSQL".equals(p.dialectName()) && "2008".equals(p.dialectVersion()));

        assertThat(foundStandardSQL)
                .as("StandardSQLDialectPlugin should be discoverable via ServiceLoader")
                .isTrue();
    }

    @Test
    void shouldBeRegisteredInRegistry() {
        SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();

        assertThat(registry.isSupported("StandardSQL")).isTrue();
        assertThat(registry.isSupported("standardsql")).isTrue(); // case-insensitive
    }

    @Test
    void shouldProvideRendererViaRegistry() {
        SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();

        RegistryResult<SqlRenderer> result = registry.getRenderer("StandardSQL", "2008");

        assertThat(result).isInstanceOf(RegistryResult.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldProvideRendererViaRegistryCaseInsensitive() {
        SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();

        // Test various case combinations
        RegistryResult<SqlRenderer> result1 = registry.getRenderer("standardsql", "2008");
        RegistryResult<SqlRenderer> result2 = registry.getRenderer("STANDARDSQL", "2008");
        RegistryResult<SqlRenderer> result3 = registry.getRenderer("StandardSQL", "2008");

        assertThat(result1).isInstanceOf(RegistryResult.Success.class);
        assertThat(result2).isInstanceOf(RegistryResult.Success.class);
        assertThat(result3).isInstanceOf(RegistryResult.Success.class);
    }

    @Test
    void shouldUseExactVersionMatching() {
        SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();

        // Exact match should work
        RegistryResult<SqlRenderer> exactMatch = registry.getRenderer("StandardSQL", "2008");
        assertThat(exactMatch).isInstanceOf(RegistryResult.Success.class);

        // Different version should fail (non-SemVer uses exact matching)
        RegistryResult<SqlRenderer> differentVersion = registry.getRenderer("StandardSQL", "2011");
        assertThat(differentVersion).isInstanceOf(RegistryResult.Failure.class);
    }

    @Test
    void shouldProvideRendererWithoutVersion() {
        SqlDialectRegistry registry = SqlDialectRegistry.createWithServiceLoader();

        // When version is not specified, should return the first available plugin
        RegistryResult<SqlRenderer> result = registry.getRenderer("StandardSQL");

        assertThat(result).isInstanceOf(RegistryResult.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }
}
