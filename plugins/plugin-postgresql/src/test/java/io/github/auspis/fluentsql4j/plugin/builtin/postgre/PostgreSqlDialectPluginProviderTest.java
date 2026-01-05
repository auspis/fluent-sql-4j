package io.github.auspis.fluentsql4j.plugin.builtin.postgre;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.builtin.postgre.PostgreSqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.builtin.postgre.PostgreSqlDialectPluginProvider;

class PostgreSqlDialectPluginProviderTest {

    @Test
    void shouldReturnPostgreSQLDialectPlugin() {
        PostgreSqlDialectPluginProvider provider = new PostgreSqlDialectPluginProvider();
        SqlDialectPlugin plugin = provider.get();

        assertThat(plugin).isNotNull();
        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(plugin.dialectVersion()).isEqualTo("^15.0.0");
    }

    @Test
    void shouldReturnSameInstanceAsPostgreSQLDialectPlugin() {
        PostgreSqlDialectPluginProvider provider = new PostgreSqlDialectPluginProvider();
        SqlDialectPlugin pluginFromProvider = provider.get();
        SqlDialectPlugin pluginFromFactory = PostgreSqlDialectPlugin.instance();

        assertThat(pluginFromProvider).isSameAs(pluginFromFactory);
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // Test that multiple providers return the same singleton instance
        PostgreSqlDialectPluginProvider[] providers = new PostgreSqlDialectPluginProvider[5];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            providers[i] = new PostgreSqlDialectPluginProvider();
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
        PostgreSqlDialectPluginProvider provider = new PostgreSqlDialectPluginProvider();

        // Verify it implements the interface
        assertThat(provider).isInstanceOf(io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider.class);
    }
}
