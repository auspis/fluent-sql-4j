package lan.tlab.r4j.jdsql.dsl.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import lan.tlab.r4j.jdsql.dsl.DSL;
import lan.tlab.r4j.jdsql.dsl.DSLRegistry;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.dsl.MysqlDSL;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

/**
 * Integration test verifying that MySQLDSL is correctly integrated
 * into the plugin system and can be retrieved via DSLRegistry.
 */
class MysqlDSLIntegrationTest {

    @Test
    void shouldRetrieveMySQLDSLFromRegistry() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        DSL dsl = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow();

        // Verify we got MySQLDSL, not base DSL
        assertThat(dsl).isInstanceOf(MysqlDSL.class);
    }

    @Test
    void shouldRetrieveMySQLDSLForAnyMySQL8Version() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // Test various MySQL 8.x versions
        assertThat(registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "8.0.0").orElseThrow())
                .isInstanceOf(MysqlDSL.class);
        assertThat(registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "8.0.35").orElseThrow())
                .isInstanceOf(MysqlDSL.class);
        assertThat(registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "8.1.0").orElseThrow())
                .isInstanceOf(MysqlDSL.class);
        assertThat(registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "8.999.999").orElseThrow())
                .isInstanceOf(MysqlDSL.class);
    }

    @Test
    void shouldNotMatchMySQL9() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // MySQL 9.x should not match the ^8.0.0 version range
        assertThat(registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "9.0.0").isFailure())
                .isTrue();
    }

    @Test
    void shouldNotMatchMySQL7() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        // MySQL 7.x should not match the ^8.0.0 version range
        assertThat(registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "7.0.0").isFailure())
                .isTrue();
    }

    @Test
    void mysqlDslHasStandardAndCustomMethodsToo() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        MysqlDSL mysqlDsl = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "8.0.35", MysqlDSL.class)
                .orElseThrow();

        assertThat(mysqlDsl).isNotNull();

        assertThat(mysqlDsl.select("name")).isNotNull();
        assertThat(mysqlDsl.insertInto("users")).isNotNull();

        assertThat(mysqlDsl.groupConcat("names", ",")).isNotNull();
    }

    @Test
    void shouldHaveMySQLRendererConfigured() throws SQLException {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        MysqlDSL dsl = registry.dslFor(MysqlDialectPlugin.DIALECT_NAME, "8.0.35", MysqlDSL.class)
                .orElseThrow();

        // Verify the specFactory is not null and properly configured
        assertThat(dsl.getSpecFactory()).isNotNull();

        // Build a simple query to verify specFactory works
        Connection connection = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        when(connection.prepareStatement(sqlCaptor.capture())).thenReturn(ps);

        dsl.select("name", "email").from("users").buildPreparedStatement(connection);

        // MySQL uses backticks for identifiers
        assertThat(sqlCaptor.getValue()).contains("`name`");
        assertThat(sqlCaptor.getValue()).contains("`email`");
        assertThat(sqlCaptor.getValue()).contains("`users`");
    }
}
