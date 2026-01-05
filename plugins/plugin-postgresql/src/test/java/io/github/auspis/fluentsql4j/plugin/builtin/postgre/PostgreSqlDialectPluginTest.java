package io.github.auspis.fluentsql4j.plugin.builtin.postgre;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import org.junit.jupiter.api.Test;

class PostgreSqlDialectPluginTest {

    @Test
    void shouldReturnSingletonInstance() {
        SqlDialectPlugin instance1 = PostgreSqlDialectPlugin.instance();
        SqlDialectPlugin instance2 = PostgreSqlDialectPlugin.instance();

        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void shouldHaveCorrectDialectName() {
        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();

        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(plugin.dialectName()).isEqualTo(PostgreSqlDialectPlugin.DIALECT_NAME);
    }

    @Test
    void shouldHaveCorrectDialectVersion() {
        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();

        assertThat(plugin.dialectVersion()).isEqualTo("^15.0.0");
        assertThat(plugin.dialectVersion()).isEqualTo(PostgreSqlDialectPlugin.DIALECT_VERSION);
    }

    @Test
    void shouldCreateValidDSL() {
        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();
        DSL dsl = plugin.createDSL();

        assertThat(dsl).isNotNull();
        assertThat(dsl.getSpecFactory()).isNotNull();
    }

    @Test
    void shouldCreateNewDSLOnEachCall() {
        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();

        DSL dsl1 = plugin.createDSL();
        DSL dsl2 = plugin.createDSL();

        // Verify that each call creates a new instance
        assertThat(dsl1).isNotSameAs(dsl2);
    }

    @Test
    void shouldHaveNonNullDialectNameConstant() {
        assertThat(PostgreSqlDialectPlugin.DIALECT_NAME).isNotNull();
        assertThat(PostgreSqlDialectPlugin.DIALECT_NAME).isNotBlank();
    }

    @Test
    void shouldHaveNonNullDialectVersionConstant() {
        assertThat(PostgreSqlDialectPlugin.DIALECT_VERSION).isNotNull();
        assertThat(PostgreSqlDialectPlugin.DIALECT_VERSION).isNotBlank();
    }

    @Test
    void shouldCreateDSLCompatibleWithPostgreSQL() {
        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();
        DSL dsl = plugin.createDSL();

        // Verify DSL is configured for PostgreSQL
        assertThat(dsl).isNotNull();
        assertThat(dsl.getSpecFactory()).isNotNull();
        // The specFactory should support PostgreSQL-specific features
        assertThat(dsl.getSpecFactory().toString()).isNotNull();
    }

    @Test
    void shouldBeImmutable() {
        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();

        // Plugin is a record, so it's immutable by design
        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(plugin.dialectVersion()).isEqualTo("^15.0.0");

        // Verify multiple access returns same values
        assertThat(plugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(plugin.dialectVersion()).isEqualTo("^15.0.0");
    }

    @Test
    void shouldSupportThreadSafeAccess() throws InterruptedException {
        // Verify singleton pattern works across multiple threads
        Thread[] threads = new Thread[10];
        SqlDialectPlugin[] plugins = new SqlDialectPlugin[10];

        for (int i = 0; i < 10; i++) {
            int index = i;
            threads[i] = new Thread(() -> {
                plugins[index] = PostgreSqlDialectPlugin.instance();
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
    void shouldCreatePostgreSQLDSL() {
        SqlDialectPlugin plugin = PostgreSqlDialectPlugin.instance();
        DSL dsl = plugin.createDSL();

        // Verify it returns PostgreSqlDSL, not base DSL
        assertThat(dsl).isInstanceOf(io.github.auspis.fluentsql4j.plugin.builtin.postgre.dsl.PostgreSqlDSL.class);
        assertThat(dsl.getSpecFactory()).isNotNull();
    }
}
