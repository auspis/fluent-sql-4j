package lan.tlab.r4j.jdsql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.ServiceLoader;
import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginProvider;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;
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
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        assertThat(registry.isSupported("StandardSQL")).isTrue();
        assertThat(registry.isSupported("standardsql")).isTrue(); // case-insensitive
    }

    @Test
    void shouldProvideRendererViaRegistry() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        Result<DialectRenderer> result = registry.getDialectRenderer("StandardSQL", "2008");

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void shouldProvideRendererViaRegistryCaseInsensitive() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Test various case combinations
        Result<DialectRenderer> result1 = registry.getDialectRenderer("standardsql", "2008");
        Result<DialectRenderer> result2 = registry.getDialectRenderer("STANDARDSQL", "2008");
        Result<DialectRenderer> result3 = registry.getDialectRenderer("StandardSQL", "2008");

        assertThat(result1).isInstanceOf(Result.Success.class);
        assertThat(result2).isInstanceOf(Result.Success.class);
        assertThat(result3).isInstanceOf(Result.Success.class);
    }

    @Test
    void shouldUseExactVersionMatching() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Exact match should work
        Result<DialectRenderer> exactMatch = registry.getDialectRenderer("StandardSQL", "2008");
        assertThat(exactMatch).isInstanceOf(Result.Success.class);

        // Different version should fail (non-SemVer uses exact matching)
        Result<DialectRenderer> differentVersion = registry.getDialectRenderer("StandardSQL", "2011");
        assertThat(differentVersion).isInstanceOf(Result.Failure.class);
    }

    @Test
    void shouldProvideRendererWithoutVersion() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // When version is not specified, should return the first available plugin
        Result<DialectRenderer> result = registry.getRenderer("StandardSQL");

        assertThat(result).isInstanceOf(Result.Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }
}
