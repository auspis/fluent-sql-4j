package lan.tlab.r4j.sql.plugin.builtin.postgresql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.ServiceLoader;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;
import org.junit.jupiter.api.Test;

class PostgreSQLDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        List<SqlDialectPlugin> plugins = serviceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .map(SqlDialectPluginProvider::get)
                .toList();

        // Verify PostgreSQL plugin is discovered
        assertThat(plugins).anyMatch(plugin -> "PostgreSQL".equalsIgnoreCase(plugin.dialectName()));
    }

    @Test
    void shouldLoadPostgreSQLDialectPluginProvider() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        List<SqlDialectPluginProvider> providers =
                serviceLoader.stream().map(ServiceLoader.Provider::get).toList();

        // Verify PostgreSQLDialectPluginProvider is in the list
        assertThat(providers).anyMatch(provider -> provider instanceof PostgreSQLDialectPluginProvider);
    }

    @Test
    void shouldProvideValidPluginViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin postgresqlPlugin = serviceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .map(SqlDialectPluginProvider::get)
                .filter(plugin -> "PostgreSQL".equalsIgnoreCase(plugin.dialectName()))
                .findFirst()
                .orElse(null);

        assertThat(postgresqlPlugin).isNotNull();
        assertThat(postgresqlPlugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(postgresqlPlugin.dialectVersion()).isEqualTo(">=12.0.0 <17.0.0");
        assertThat(postgresqlPlugin.createRenderer()).isNotNull();
    }
}
