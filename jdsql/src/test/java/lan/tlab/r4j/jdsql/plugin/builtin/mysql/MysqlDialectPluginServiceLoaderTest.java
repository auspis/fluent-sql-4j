package lan.tlab.r4j.jdsql.plugin.builtin.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;
import lan.tlab.r4j.sql.plugin.builtin.mysql.MysqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.builtin.mysql.MysqlDialectPluginProvider;
import org.junit.jupiter.api.Test;

class MysqlDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                .anyMatch(provider -> provider instanceof MysqlDialectPluginProvider);

        assertThat(found).isTrue();
    }

    @Test
    void shouldProvideCorrectPluginViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin mysqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(provider -> provider instanceof MysqlDialectPluginProvider)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        assertThat(mysqlPlugin).isNotNull();
        assertThat(mysqlPlugin.dialectName()).isEqualTo("MySQL");
        assertThat(mysqlPlugin.dialectVersion()).isEqualTo("^8.0.0");
    }

    @Test
    void shouldReturnSingletonInstanceViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin plugin1 = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(provider -> provider instanceof MysqlDialectPluginProvider)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        SqlDialectPlugin plugin2 = MysqlDialectPlugin.instance();

        assertThat(plugin1).isSameAs(plugin2);
    }

    @Test
    void shouldLoadMultiplePluginsIncludingMySQL() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        long count = StreamSupport.stream(serviceLoader.spliterator(), false).count();

        // Should have at least StandardSQL and MySQL plugins
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldProvideWorkingRenderer() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin mysqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(provider -> provider instanceof MysqlDialectPluginProvider)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElseThrow();

        // Verify the DSL can be created
        assertThat(mysqlPlugin.createDSL()).isNotNull();
        assertThat(mysqlPlugin.createDSL().getRenderer()).isNotNull();
    }
}
