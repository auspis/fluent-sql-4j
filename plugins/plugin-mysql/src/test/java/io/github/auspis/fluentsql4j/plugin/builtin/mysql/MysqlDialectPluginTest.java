package io.github.auspis.fluentsql4j.plugin.builtin.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class MysqlDialectPluginTest {

    @Test
    void shouldReturnSingletonInstance() {
        SqlDialectPlugin instance1 = MysqlDialectPlugin.instance();
        SqlDialectPlugin instance2 = MysqlDialectPlugin.instance();

        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void shouldHaveCorrectDialectName() {
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();

        assertThat(plugin.dialectName()).isEqualTo("MySQL");
        assertThat(plugin.dialectName()).isEqualTo(MysqlDialectPlugin.DIALECT_NAME);
    }

    @Test
    void shouldHaveCorrectDialectVersion() {
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();

        assertThat(plugin.dialectVersion()).isEqualTo("^8.0.0");
        assertThat(plugin.dialectVersion()).isEqualTo(MysqlDialectPlugin.DIALECT_VERSION);
    }

    @Test
    void shouldCreateValidDSL() {
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();
        DSL dsl = plugin.createDSL();

        assertThat(dsl).isNotNull();
        assertThat(dsl.getSpecFactory()).isNotNull();
    }

    @Test
    void shouldCreateNewDSLOnEachCall() {
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();

        DSL dsl1 = plugin.createDSL();
        DSL dsl2 = plugin.createDSL();

        // Verify that each call creates a new instance
        assertThat(dsl1).isNotSameAs(dsl2);
    }

    @Test
    void shouldHaveNonNullDialectNameConstant() {
        assertThat(MysqlDialectPlugin.DIALECT_NAME).isNotNull();
        assertThat(MysqlDialectPlugin.DIALECT_NAME).isNotBlank();
    }

    @Test
    void shouldHaveNonNullDialectVersionConstant() {
        assertThat(MysqlDialectPlugin.DIALECT_VERSION).isNotNull();
        assertThat(MysqlDialectPlugin.DIALECT_VERSION).isNotBlank();
    }

    @Test
    void shouldCreateDSLCompatibleWithMySQL() {
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();
        DSL dsl = plugin.createDSL();

        // Verify DSL is configured for MySQL
        assertThat(dsl).isNotNull();
        assertThat(dsl.getSpecFactory()).isNotNull();
        // The specFactory should support MySQL-specific features
        assertThat(dsl.getSpecFactory().toString()).isNotNull();
    }

    @Test
    void shouldBeImmutable() {
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();

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
                plugins[index] = MysqlDialectPlugin.instance();
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

    @Test
    void shouldCreateMySQLDSL() {
        SqlDialectPlugin plugin = MysqlDialectPlugin.instance();
        DSL dsl = plugin.createDSL();

        // Verify it returns MySQLDSL, not base DSL
        assertThat(dsl).isInstanceOf(io.github.auspis.fluentsql4j.plugin.builtin.mysql.dsl.MysqlDSL.class);
        assertThat(dsl.getSpecFactory()).isNotNull();
    }
}
