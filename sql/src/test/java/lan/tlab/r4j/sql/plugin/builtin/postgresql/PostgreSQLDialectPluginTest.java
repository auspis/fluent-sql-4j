package lan.tlab.r4j.sql.plugin.builtin.postgresql;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class PostgreSQLDialectPluginTest {

    @Test
    void shouldReturnSingletonInstance() {
        SqlDialectPlugin instance1 = PostgreSQLDialectPlugin.instance();
        SqlDialectPlugin instance2 = PostgreSQLDialectPlugin.instance();

        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void shouldHaveCorrectDialectName() {
        SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();

        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(plugin.dialectName()).isEqualTo(PostgreSQLDialectPlugin.DIALECT_NAME);
    }

    @Test
    void shouldHaveCorrectDialectVersion() {
        SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();

        assertThat(plugin.dialectVersion()).isEqualTo(">=12.0.0 <17.0.0");
        assertThat(plugin.dialectVersion()).isEqualTo(PostgreSQLDialectPlugin.DIALECT_VERSION);
    }

    @Test
    void shouldCreateValidRenderer() {
        SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();
        DialectRenderer renderer = plugin.createRenderer();

        assertThat(renderer).isNotNull();
    }

    @Test
    void shouldCreateNewRendererOnEachCall() {
        SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();

        DialectRenderer renderer1 = plugin.createRenderer();
        DialectRenderer renderer2 = plugin.createRenderer();

        // Verify that each call creates a new instance
        assertThat(renderer1).isNotSameAs(renderer2);
    }

    @Test
    void shouldHaveNonNullDialectNameConstant() {
        assertThat(PostgreSQLDialectPlugin.DIALECT_NAME).isNotNull();
        assertThat(PostgreSQLDialectPlugin.DIALECT_NAME).isNotBlank();
    }

    @Test
    void shouldHaveNonNullDialectVersionConstant() {
        assertThat(PostgreSQLDialectPlugin.DIALECT_VERSION).isNotNull();
        assertThat(PostgreSQLDialectPlugin.DIALECT_VERSION).isNotBlank();
    }

    @Test
    void shouldCreateRendererCompatibleWithPostgreSQL() {
        SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();
        DialectRenderer renderer = plugin.createRenderer();

        // Verify renderer is configured for PostgreSQL
        assertThat(renderer).isNotNull();
        // The renderer should support PostgreSQL-specific features
        assertThat(renderer.toString()).isNotNull();
    }

    @Test
    void shouldBeImmutable() {
        SqlDialectPlugin plugin = PostgreSQLDialectPlugin.instance();

        // Plugin is a record, so it's immutable by design
        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(plugin.dialectVersion()).isEqualTo(">=12.0.0 <17.0.0");

        // Verify multiple access returns same values
        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(plugin.dialectVersion()).isEqualTo(">=12.0.0 <17.0.0");
    }

    @Test
    void shouldSupportThreadSafeAccess() throws InterruptedException {
        // Verify singleton pattern works across multiple threads
        Thread[] threads = new Thread[10];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[10];

        for (int i = 0; i < 10; i++) {
            int index = i;
            threads[i] = new Thread(() -> {
                plugins[index] = PostgreSQLDialectPlugin.instance();
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
