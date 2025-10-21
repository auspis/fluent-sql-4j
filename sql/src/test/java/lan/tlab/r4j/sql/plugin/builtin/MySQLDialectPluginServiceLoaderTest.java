package lan.tlab.r4j.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ServiceLoader;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;
import org.junit.jupiter.api.Test;

class MySQLDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> loader = ServiceLoader.load(SqlDialectPluginProvider.class);

        boolean found = false;
        for (SqlDialectPluginProvider provider : loader) {
            if (provider instanceof MySQLDialectPluginProvider) {
                found = true;
                break;
            }
        }

        assertThat(found)
                .as("MySQLDialectPluginProvider should be discoverable via ServiceLoader")
                .isTrue();
    }

    @Test
    void shouldProvideValidPluginViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> loader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin mysqlPlugin = null;
        for (SqlDialectPluginProvider provider : loader) {
            if (provider instanceof MySQLDialectPluginProvider) {
                mysqlPlugin = provider.get();
                break;
            }
        }

        assertThat(mysqlPlugin).isNotNull();
        assertThat(mysqlPlugin.dialectName()).isEqualTo("MySQL");
        assertThat(mysqlPlugin.dialectVersion()).isEqualTo("^8.0.0");
    }

    @Test
    void shouldProvideWorkingRenderer() {
        ServiceLoader<SqlDialectPluginProvider> loader = ServiceLoader.load(SqlDialectPluginProvider.class);

        for (SqlDialectPluginProvider provider : loader) {
            if (provider instanceof MySQLDialectPluginProvider) {
                SqlDialectPlugin plugin = provider.get();
                assertThat(plugin.createRenderer()).isNotNull();
                return;
            }
        }

        throw new AssertionError("MySQLDialectPluginProvider not found via ServiceLoader");
    }
}
