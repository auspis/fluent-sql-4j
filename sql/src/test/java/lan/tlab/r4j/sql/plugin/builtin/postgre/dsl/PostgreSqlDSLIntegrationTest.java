package lan.tlab.r4j.sql.plugin.builtin.postgre.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.dsl.DSL;
import lan.tlab.r4j.sql.dsl.DSLRegistry;
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
        assertThat(registry.dslFor("postgresql", "15.0.0").orElseThrow())
                .isInstanceOf(PostgreSqlDSL.class);
        assertThat(registry.dslFor("postgresql", "15.2.0").orElseThrow())
                .isInstanceOf(PostgreSqlDSL.class);
        assertThat(registry.dslFor("postgresql", "15.9.0").orElseThrow())
                .isInstanceOf(PostgreSqlDSL.class);
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

    @Test
    void shouldHavePostgreSQLRendererConfigured() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        PostgreSqlDSL dsl =
                (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();

        // Verify the renderer is not null and properly configured
        assertThat(dsl.getRenderer()).isNotNull();

        // Build a simple query to verify renderer works
        String sql = dsl.select("name", "email").from("users").build();
        assertThat(sql).isNotNull();
        assertThat(sql).contains("SELECT");
        assertThat(sql).contains("FROM");
    }

    @Test
    void shouldRenderStringAggCorrectly() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        PostgreSqlDSL dsl =
                (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();

        String sql = dsl.select()
                .expression(dsl.stringAgg("name").separator(", ").build())
                .as("names")
                .from("users")
                .build();

        assertThat(sql).contains("STRING_AGG");
        assertThat(sql).contains("', '");
        assertThat(sql).contains("names");
    }

    @Test
    void shouldRenderStringAggWithOrderBy() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        PostgreSqlDSL dsl =
                (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();

        String sql = dsl.select()
                .expression(
                        dsl.stringAgg("name").separator(", ").orderBy("name DESC").build())
                .as("names")
                .from("users")
                .build();

        assertThat(sql).contains("STRING_AGG");
        assertThat(sql).contains("ORDER BY name DESC");
    }

    @Test
    void shouldRenderStringAggWithDistinct() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        PostgreSqlDSL dsl =
                (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();

        String sql = dsl.select()
                .expression(dsl.stringAgg("category").distinct().separator(" | ").build())
                .as("categories")
                .from("products")
                .build();

        assertThat(sql).contains("STRING_AGG(DISTINCT");
        assertThat(sql).contains(" | ");
    }

    @Test
    void shouldRenderArrayAgg() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        PostgreSqlDSL dsl =
                (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();

        String sql = dsl.select()
                .expression(dsl.arrayAgg("tag").orderBy("tag").build())
                .as("tags")
                .from("post_tags")
                .build();

        assertThat(sql).contains("ARRAY_AGG");
        assertThat(sql).contains("ORDER BY tag");
    }

    @Test
    void shouldRenderJsonbAgg() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        PostgreSqlDSL dsl =
                (PostgreSqlDSL) registry.dslFor("postgresql", "15.0.0").orElseThrow();

        String sql = dsl.select()
                .expression(dsl.jsonbAgg("data").build())
                .as("json_data")
                .from("events")
                .build();

        assertThat(sql).contains("JSONB_AGG");
    }
}
