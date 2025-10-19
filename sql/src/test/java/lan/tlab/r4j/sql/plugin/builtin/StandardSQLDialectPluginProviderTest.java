package lan.tlab.r4j.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class StandardSQLDialectPluginProviderTest {

    @Test
    void shouldReturnStandardSQLDialectPlugin() {
        StandardSQLDialectPluginProvider provider = new StandardSQLDialectPluginProvider();
        SqlDialectPlugin plugin = provider.get();

        assertThat(plugin).isNotNull();
        assertThat(plugin.dialectName()).isEqualTo("StandardSQL");
        assertThat(plugin.dialectVersion()).isEqualTo("2008");
    }

    @Test
    void shouldReturnSameInstanceAsStandardSQLDialectPlugin() {
        StandardSQLDialectPluginProvider provider = new StandardSQLDialectPluginProvider();
        SqlDialectPlugin pluginFromProvider = provider.get();
        SqlDialectPlugin pluginFromFactory = StandardSQLDialectPlugin.instance();

        assertThat(pluginFromProvider).isSameAs(pluginFromFactory);
    }

    @Test
    void shouldBeThreadSafe() throws InterruptedException {
        // Test that multiple providers return the same singleton instance
        StandardSQLDialectPluginProvider[] providers = new StandardSQLDialectPluginProvider[5];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[5];
        Thread[] threads = new Thread[5];

        for (int i = 0; i < 5; i++) {
            providers[i] = new StandardSQLDialectPluginProvider();
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
        StandardSQLDialectPluginProvider provider = new StandardSQLDialectPluginProvider();

        // Verify it implements the interface
        assertThat(provider).isInstanceOf(lan.tlab.r4j.sql.plugin.SqlDialectPluginProvider.class);
    }
}
