package io.github.massimiliano.fluentsql4j.plugin.builtin.postgre;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPluginProvider;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

class PostgreSqlDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                .anyMatch(provider -> provider instanceof PostgreSqlDialectPluginProvider);

        assertThat(found).isTrue();
    }

    @Test
    void shouldProvideCorrectPluginViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin postgresqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(provider -> provider instanceof PostgreSqlDialectPluginProvider)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        assertThat(postgresqlPlugin).isNotNull();
        assertThat(postgresqlPlugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(postgresqlPlugin.dialectVersion()).isEqualTo("^15.0.0");
    }

    @Test
    void shouldReturnSingletonInstanceViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin plugin1 = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(provider -> provider instanceof PostgreSqlDialectPluginProvider)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        SqlDialectPlugin plugin2 = PostgreSqlDialectPlugin.instance();

        assertThat(plugin1).isSameAs(plugin2);
    }

    @Test
    void shouldLoadMultiplePluginsIncludingPostgreSQL() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        long count = StreamSupport.stream(serviceLoader.spliterator(), false).count();

        // Should have at least StandardSQL and PostgreSQL plugins
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldProvideWorkingRenderer() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin postgresqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(provider -> provider instanceof PostgreSqlDialectPluginProvider)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElseThrow();

        // Verify the DSL can be created
        assertThat(postgresqlPlugin.createDSL()).isNotNull();
        assertThat(postgresqlPlugin.createDSL().getSpecFactory()).isNotNull();
    }
}
