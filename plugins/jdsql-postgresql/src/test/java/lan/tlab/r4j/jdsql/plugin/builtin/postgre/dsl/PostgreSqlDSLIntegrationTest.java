package lan.tlab.r4j.jdsql.plugin.builtin.postgre.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.DSLRegistry;
import org.junit.jupiter.api.Test;

/**
 * Integration test verifying that PostgreSqlDSL is correctly integrated
 * into the plugin system and can be retrieved via DSLRegistry.
 */
class PostgreSqlDSLIntegrationTest {

    @Test
    void shouldRetrievePostgreSQLDSLFromRegistry() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        DSL dsl = registry.dslFor("postgresql", "15.0.0").orElseThrow();

        // Verify we got PostgreSqlDSL, not base DSL
        assertThat(dsl).isInstanceOf(PostgreSqlDSL.class);
    }

    @Test
    void shouldRetrievePostgreSQLDSLForAnyPostgreSQL15Version() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // Test various PostgreSQL 15.x versions
        assertThat(registry.dslFor("postgresql", "15.0.0").orElseThrow()).isInstanceOf(PostgreSqlDSL.class);
        assertThat(registry.dslFor("postgresql", "15.2.0").orElseThrow()).isInstanceOf(PostgreSqlDSL.class);
        assertThat(registry.dslFor("postgresql", "15.9.0").orElseThrow()).isInstanceOf(PostgreSqlDSL.class);
    }

    @Test
    void shouldNotMatchPostgreSQL16() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // PostgreSQL 16.x should not match the ^15.0.0 version range
        assertThat(registry.dslFor("postgresql", "16.0.0").isFailure()).isTrue();
    }

    @Test
    void shouldNotMatchPostgreSQL14() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // PostgreSQL 14.x should not match the ^15.0.0 version range
        assertThat(registry.dslFor("postgresql", "14.0.0").isFailure()).isTrue();
    }

    @Test
    void shouldCastToPostgreSQLDSLAndUseCustomMethods() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        DSL baseDsl = registry.dslFor("postgresql", "15.0.0").orElseThrow();
        PostgreSqlDSL postgresDsl = (PostgreSqlDSL) baseDsl;

        // Verify we can access PostgreSQL-specific methods
        assertThat(postgresDsl).isNotNull();
        assertThat(postgresDsl.stringAgg("name")).isNotNull();
        assertThat(postgresDsl.arrayAgg("tags")).isNotNull();

        // Verify base DSL methods still work
        assertThat(postgresDsl.select("name")).isNotNull();
        assertThat(postgresDsl.insertInto("users")).isNotNull();
    }
}
