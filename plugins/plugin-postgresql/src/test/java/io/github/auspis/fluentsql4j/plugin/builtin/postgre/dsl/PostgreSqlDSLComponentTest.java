package io.github.auspis.fluentsql4j.plugin.builtin.postgre.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.dsl.DSLRegistry;
import io.github.auspis.fluentsql4j.plugin.builtin.postgre.PostgreSqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.builtin.postgre.dsl.PostgreSqlDSL;
import io.github.massimiliano.fluentsql4j.test.util.annotation.ComponentTest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Component test verifying that PostgreSqlDSL is correctly integrated
 * into the plugin system and can be retrieved via DSLRegistry.
 */
@ComponentTest
class PostgreSqlDSLComponentTest {

    @Test
    void shouldRetrievePostgreSQLDSLFromRegistry() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        DSL dsl =
                registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "15.0.0").orElseThrow();

        // Verify we got PostgreSqlDSL, not base DSL
        assertThat(dsl).isInstanceOf(PostgreSqlDSL.class);
    }

    @Test
    void shouldRetrievePostgreSQLDSLForAnyPostgreSQL15Version() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // Test various PostgreSQL 15.x versions
        assertThat(registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "15.0.0")
                        .orElseThrow())
                .isInstanceOf(PostgreSqlDSL.class);
        assertThat(registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "15.2.0")
                        .orElseThrow())
                .isInstanceOf(PostgreSqlDSL.class);
        assertThat(registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "15.9.0")
                        .orElseThrow())
                .isInstanceOf(PostgreSqlDSL.class);
        assertThat(registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "15.999.999")
                        .orElseThrow())
                .isInstanceOf(PostgreSqlDSL.class);
    }

    @Test
    void shouldNotMatchPostgreSQL16() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // PostgreSQL 16.x should not match the ^15.0.0 version range
        assertThat(registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "16.0.0")
                        .isFailure())
                .isTrue();
    }

    @Test
    void shouldNotMatchPostgreSQL14() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // PostgreSQL 14.x should not match the ^15.0.0 version range
        assertThat(registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "14.0.0")
                        .isFailure())
                .isTrue();
    }

    @Test
    void postgreSqlDslHasStandardAndCustomMethodsToo() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        PostgreSqlDSL postgresDsl = registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "15.0.0", PostgreSqlDSL.class)
                .orElseThrow();

        assertThat(postgresDsl).isNotNull();

        assertThat(postgresDsl.select("name")).isNotNull();
        assertThat(postgresDsl.insertInto("users")).isNotNull();

        assertThat(postgresDsl.stringAgg("name")).isNotNull();
        assertThat(postgresDsl.arrayAgg("tags")).isNotNull();
    }

    @Test
    void shouldHavePostgreSQLRendererConfigured() throws SQLException {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        PostgreSqlDSL dsl = registry.dslFor(PostgreSqlDialectPlugin.DIALECT_NAME, "15.0.0", PostgreSqlDSL.class)
                .orElseThrow();

        // Verify the specFactory is not null and properly configured
        assertThat(dsl.getSpecFactory()).isNotNull();

        // Build a simple query to verify specFactory works
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);

        dsl.select("name", "email").from("users").build(connection);

        // PostgreSQL uses double quotes for identifiers
        assertThat(sqlCaptor.getValue()).contains("\"name\"");
        assertThat(sqlCaptor.getValue()).contains("\"email\"");
        assertThat(sqlCaptor.getValue()).contains("\"users\"");
    }
}
