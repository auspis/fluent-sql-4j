package lan.tlab.r4j.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class MySQLDialectPluginProviderTest {

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
        SqlDialectPlugin pluginFromProvider = provider.get();
        SqlDialectPlugin pluginFromFactory = MySQLDialectPlugin.instance();

        assertThat(pluginFromProvider).isSameAs(pluginFromFactory);
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // Test that multiple providers return the same singleton instance
        MySQLDialectPluginProvider[] providers = new MySQLDialectPluginProvider[5];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            providers[i] = new MySQLDialectPluginProvider();
            int index = i;
            threads[i] = new Thread(() -> {
                plugins[index] = providers[index].get();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All should return the same singleton instance
        SqlDialectPlugin firstPlugin = plugins[0];
        for (SqlDialectPlugin plugin : plugins) {
            assertThat(plugin).isSameAs(firstPlugin);
        }
    }

    @Test
    void shouldImplementSqlDialectPluginProvider() {
        MySQLDialectPluginProvider provider = new MySQLDialectPluginProvider();

        // Verify it implements the interface
        assertThat(provider).isInstanceOf(lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider.class);
    }
}
