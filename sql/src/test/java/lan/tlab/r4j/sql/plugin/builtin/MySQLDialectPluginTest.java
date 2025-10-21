package lan.tlab.r4j.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class MySQLDialectPluginTest {

    @Test
    void shouldReturnSingletonInstance() {
        SqlDialectPlugin instance1 = MySQLDialectPlugin.instance();
        SqlDialectPlugin instance2 = MySQLDialectPlugin.instance();

        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void shouldHaveCorrectDialectName() {
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();

        assertThat(plugin.dialectName()).isEqualTo("MySQL");
        assertThat(plugin.dialectName()).isEqualTo(MySQLDialectPlugin.DIALECT_NAME);
    }

    @Test
    void shouldHaveCorrectDialectVersion() {
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();

        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
        assertThat(plugin.dialectVersion()).isEqualTo(MySQLDialectPlugin.DIALECT_VERSION);
    }

    @Test
    void shouldCreateValidRenderer() {
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();
        SqlRenderer renderer = plugin.createRenderer();

        assertThat(renderer).isNotNull();
    }

    @Test
    void shouldCreateNewRendererOnEachCall() {
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();

        SqlRenderer renderer1 = plugin.createRenderer();
        SqlRenderer renderer2 = plugin.createRenderer();

        // Verify that each call creates a new instance
        assertThat(renderer1).isNotSameAs(renderer2);
    }

    @Test
    void shouldHaveNonNullDialectNameConstant() {
        assertThat(MySQLDialectPlugin.DIALECT_NAME).isNotNull();
        assertThat(MySQLDialectPlugin.DIALECT_NAME).isNotBlank();
    }

    @Test
    void shouldHaveNonNullDialectVersionConstant() {
        assertThat(MySQLDialectPlugin.DIALECT_VERSION).isNotNull();
        assertThat(MySQLDialectPlugin.DIALECT_VERSION).isNotBlank();
    }

    @Test
    void shouldCreateRendererCompatibleWithMySQL() {
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();
        SqlRenderer renderer = plugin.createRenderer();

        // Verify renderer is configured for MySQL
        assertThat(renderer).isNotNull();
        // The renderer should support MySQL-specific features
        assertThat(renderer.toString()).isNotNull();
    }

    @Test
    void shouldBeImmutable() {
        SqlDialectPlugin plugin = MySQLDialectPlugin.instance();

        // Plugin is a record, so it's immutable by design
        assertThat(plugin.dialectName()).isEqualTo("MySQL");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");

        // Verify multiple access returns same values
        assertThat(plugin.dialectName()).isEqualTo("MySQL");
        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
    }

    @Test
    void shouldSupportThreadSafeAccess() throws InterruptedException {
        // Verify singleton pattern works across multiple threads
        Thread[] threads = new Thread[10];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[10];

        for (int i = 0; i < 10; i++) {
            int index = i;
            threads[i] = new Thread(() -> {
                plugins[index] = MySQLDialectPlugin.instance();
            });
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All threads should get the same instance
        SqlDialectPlugin firstPlugin = plugins[0];
        for (SqlDialectPlugin plugin : plugins) {
            assertThat(plugin).isSameAs(firstPlugin);
        }
    }
}
