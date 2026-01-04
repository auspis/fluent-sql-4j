package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class MysqlDialectPluginProviderTest {

    @Test
    void shouldReturnMySQLDialectPlugin() {
        MysqlDialectPluginProvider provider = new MysqlDialectPluginProvider();
        SqlDialectPlugin plugin = provider.get();

        assertThat(plugin).isNotNull();
        assertThat(plugin.dialectName()).isEqualTo("MySQL");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
    }

    @Test
    void shouldReturnSameInstanceAsMySQLDialectPlugin() {
        MysqlDialectPluginProvider provider = new MysqlDialectPluginProvider();
        SqlDialectPlugin pluginFromProvider = provider.get();
        SqlDialectPlugin pluginFromFactory = MysqlDialectPlugin.instance();

        assertThat(pluginFromProvider).isSameAs(pluginFromFactory);
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // Test that multiple providers return the same singleton instance
        MysqlDialectPluginProvider[] providers = new MysqlDialectPluginProvider[5];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            providers[i] = new MysqlDialectPluginProvider();
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
        MysqlDialectPluginProvider provider = new MysqlDialectPluginProvider();

        // Verify it implements the interface
        assertThat(provider).isInstanceOf(io.github.massimiliano.fluentsql4j.plugin.SqlDialectPluginProvider.class);
    }
}
