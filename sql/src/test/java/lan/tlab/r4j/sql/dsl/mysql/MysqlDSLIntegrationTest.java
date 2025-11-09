package lan.tlab.r4j.sql.dsl.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.DSLRegistry;
import lan.tlab.r4j.sql.plugin.builtin.mysql.dsl.MysqlDSL;
import org.junit.jupiter.api.Test;

/**
 * Integration test verifying that MySQLDSL is correctly integrated
 * into the plugin system and can be retrieved via DSLRegistry.
 */
class MysqlDSLIntegrationTest {

    @Test
    void shouldRetrieveMySQLDSLFromRegistry() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        DSL dsl = registry.dslFor("mysql", "8.0.35").orElseThrow();

        // Verify we got MySQLDSL, not base DSL
        assertThat(dsl).isInstanceOf(MysqlDSL.class);
    }

    @Test
    void shouldRetrieveMySQLDSLForAnyMySQL8Version() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // Test various MySQL 8.x versions
        assertThat(registry.dslFor("mysql", "8.0.0").orElseThrow()).isInstanceOf(MysqlDSL.class);
        assertThat(registry.dslFor("mysql", "8.0.35").orElseThrow()).isInstanceOf(MysqlDSL.class);
        assertThat(registry.dslFor("mysql", "8.1.0").orElseThrow()).isInstanceOf(MysqlDSL.class);
        assertThat(registry.dslFor("mysql", "8.999.999").orElseThrow()).isInstanceOf(MysqlDSL.class);
    }

    @Test
    void shouldNotMatchMySQL9() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // MySQL 9.x should not match the ^8.0.0 version range
        assertThat(registry.dslFor("mysql", "9.0.0").isFailure()).isTrue();
    }

    @Test
    void shouldNotMatchMySQL7() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // MySQL 7.x should not match the ^8.0.0 version range
        assertThat(registry.dslFor("mysql", "7.0.0").isFailure()).isTrue();
    }

    @Test
    void shouldCastToMySQLDSLAndUseCustomMethods() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        DSL baseDsl = registry.dslFor("mysql", "8.0.35").orElseThrow();
        MysqlDSL mysqlDsl = (MysqlDSL) baseDsl;

        // Verify we can access MySQL-specific methods
        // (They will throw UnsupportedOperationException until Task 8 is complete)
        assertThat(mysqlDsl).isNotNull();

        // Verify base DSL methods still work
        assertThat(mysqlDsl.select("name")).isNotNull();
        assertThat(mysqlDsl.insertInto("users")).isNotNull();
    }

    @Test
    void shouldHaveMySQLRendererConfigured() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        MysqlDSL dsl = (MysqlDSL) registry.dslFor("mysql", "8.0.35").orElseThrow();

        // Verify the renderer is not null and properly configured
        assertThat(dsl.getRenderer()).isNotNull();

        // Build a simple query to verify renderer works
        String sql = dsl.select("name", "email").from("users").build();

        // MySQL uses backticks for identifiers
        assertThat(sql).contains("`name`");
        assertThat(sql).contains("`email`");
        assertThat(sql).contains("`users`");
    }
}
