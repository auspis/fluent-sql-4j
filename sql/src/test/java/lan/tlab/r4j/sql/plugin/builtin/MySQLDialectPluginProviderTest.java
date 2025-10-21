package lan.tlab.r4j.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider;
import org.junit.jupiter.api.Test;

class MySQLDialectPluginProviderTest {

    @Test
    void shouldImplementSqlDialectPluginProvider() {
        MySQLDialectPluginProvider provider = new MySQLDialectPluginProvider();
        assertThat(provider).isInstanceOf(SqlDialectPluginProvider.class);
    }

    @Test
    void shouldReturnMySQLDialectPlugin() {
        MySQLDialectPluginProvider provider = new MySQLDialectPluginProvider();
        SqlDialectPlugin plugin = provider.get();

        assertThat(plugin).isNotNull();
        assertThat(plugin.dialectName()).isEqualTo("MySQL");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
    }

    @Test
    void shouldReturnSameInstanceAsMySQLDialectPlugin() {
        MySQLDialectPluginProvider provider = new MySQLDialectPluginProvider();
        SqlDialectPlugin fromProvider = provider.get();
        SqlDialectPlugin fromPlugin = MySQLDialectPlugin.instance();

        assertThat(fromProvider).isSameAs(fromPlugin);
    }

    @Test
    void shouldReturnConsistentPlugin() {
        MySQLDialectPluginProvider provider = new MySQLDialectPluginProvider();

        SqlDialectPlugin plugin1 = provider.get();
        SqlDialectPlugin plugin2 = provider.get();

        assertThat(plugin1).isSameAs(plugin2);
    }

    @Test
    void shouldBeStateless() {
        MySQLDialectPluginProvider provider1 = new MySQLDialectPluginProvider();
        MySQLDialectPluginProvider provider2 = new MySQLDialectPluginProvider();

        // Different provider instances should return the same plugin instance
        assertThat(provider1.get()).isSameAs(provider2.get());
    }
}
