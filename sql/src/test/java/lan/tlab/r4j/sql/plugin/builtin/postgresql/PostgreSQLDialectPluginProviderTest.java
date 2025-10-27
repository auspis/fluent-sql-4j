package lan.tlab.r4j.sql.plugin.builtin.postgresql;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;
import org.junit.jupiter.api.Test;

class PostgreSQLDialectPluginProviderTest {

    @Test
    void shouldProvidePostgreSQLDialectPlugin() {
        SqlDialectPluginProvider provider = new PostgreSQLDialectPluginProvider();
        SqlDialectPlugin plugin = provider.get();

        assertThat(plugin).isNotNull();
        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
    }

    @Test
    void shouldProvideConsistentInstance() {
        SqlDialectPluginProvider provider = new PostgreSQLDialectPluginProvider();

        SqlDialectPlugin plugin1 = provider.get();
        SqlDialectPlugin plugin2 = provider.get();

        // Should return the same singleton instance
        assertThat(plugin1).isSameAs(plugin2);
    }

    @Test
    void shouldProvideSamePluginAsDirectAccess() {
        SqlDialectPluginProvider provider = new PostgreSQLDialectPluginProvider();
        SqlDialectPlugin pluginFromProvider = provider.get();
        SqlDialectPlugin pluginFromDirect = PostgreSQLDialectPlugin.instance();

        assertThat(pluginFromProvider).isSameAs(pluginFromDirect);
    }
}
