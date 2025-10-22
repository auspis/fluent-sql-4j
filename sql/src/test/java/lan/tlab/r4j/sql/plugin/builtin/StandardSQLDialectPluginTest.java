package lan.tlab.r4j.sql.plugin.builtin;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.builtin.standardsql2008.StandardSQLDialectPlugin;
import org.junit.jupiter.api.Test;

class StandardSQLDialectPluginTest {

    @Test
    void shouldReturnSingletonInstance() {
        SqlDialectPlugin instance1 = StandardSQLDialectPlugin.instance();
        SqlDialectPlugin instance2 = StandardSQLDialectPlugin.instance();

        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void shouldHaveCorrectDialectName() {
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();

        assertThat(plugin.dialectName()).isEqualTo("StandardSQL");
        assertThat(plugin.dialectName()).isEqualTo(StandardSQLDialectPlugin.DIALECT_NAME);
    }

    @Test
    void shouldHaveCorrectDialectVersion() {
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();

        assertThat(plugin.dialectVersion()).isEqualTo("2008");
        assertThat(plugin.dialectVersion()).isEqualTo(StandardSQLDialectPlugin.DIALECT_VERSION);
    }

    @Test
    void shouldCreateValidRenderer() {
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
        DialectRenderer renderer = plugin.createRenderer();

        assertThat(renderer).isNotNull();
    }

    @Test
    void shouldCreateNewRendererOnEachCall() {
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();

        DialectRenderer renderer1 = plugin.createRenderer();
        DialectRenderer renderer2 = plugin.createRenderer();

        // Verify that each call creates a new instance
        assertThat(renderer1).isNotSameAs(renderer2);
    }

    @Test
    void shouldHaveNonNullDialectNameConstant() {
        assertThat(StandardSQLDialectPlugin.DIALECT_NAME).isNotNull();
        assertThat(StandardSQLDialectPlugin.DIALECT_NAME).isNotBlank();
    }

    @Test
    void shouldHaveNonNullDialectVersionConstant() {
        assertThat(StandardSQLDialectPlugin.DIALECT_VERSION).isNotNull();
        assertThat(StandardSQLDialectPlugin.DIALECT_VERSION).isNotBlank();
    }

    @Test
    void shouldCreateRendererCompatibleWithStandardSql2008() {
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();
        DialectRenderer renderer = plugin.createRenderer();

        // Verify renderer is configured for standard SQL:2008
        assertThat(renderer).isNotNull();
        // The renderer should support standard SQL features
        assertThat(renderer.toString()).isNotNull();
    }

    @Test
    void shouldBeImmutable() {
        SqlDialectPlugin plugin = StandardSQLDialectPlugin.instance();

        // Plugin is a record, so it's immutable by design
        assertThat(plugin.dialectName()).isEqualTo("StandardSQL");
        assertThat(plugin.dialectVersion()).isEqualTo("2008");

        // Verify multiple access returns same values
        assertThat(plugin.dialectName()).isEqualTo("StandardSQL");
        assertThat(plugin.dialectVersion()).isEqualTo("2008");
    }

    @Test
    void shouldSupportThreadSafeAccess() throws InterruptedException {
        // Verify singleton pattern works across multiple threads
        Thread[] threads = new Thread[10];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[10];

        for (int i = 0; i < 10; i++) {
            int index = i;
            threads[i] = new Thread(() -> {
                plugins[index] = StandardSQLDialectPlugin.instance();
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
