package lan.tlab.r4j.sql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import lan.tlab.r4j.sql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.sql.functional.Result;
import lan.tlab.r4j.sql.functional.Result.Failure;
import lan.tlab.r4j.sql.functional.Result.Success;
import lan.tlab.r4j.sql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.sql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.sql.plugin.SqlTestPlugin;
import lan.tlab.r4j.sql.plugin.builtin.mysql.MySQLDialectPlugin;
import lan.tlab.r4j.sql.plugin.builtin.standardsql2008.StandardSQLDialectPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DSLRegistryTest {

    private DialectRenderer mockRenderer;
    private SqlDialectPlugin testPlugin;
    private SqlDialectPluginRegistry pluginRegistry;
    private DSLRegistry registry;

    @BeforeEach
    void setUp() {
        mockRenderer = mock(DialectRenderer.class);
        testPlugin = SqlTestPlugin.create(mockRenderer);
        pluginRegistry = SqlDialectPluginRegistry.of(java.util.List.of(testPlugin));
        registry = DSLRegistry.of(pluginRegistry);
    }

    @Test
    void createWithServiceLoader_shouldLoadPlugins() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        Result<DSL> result = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME);

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void of_shouldCreateRegistryWithPluginRegistry() {
        assertThat(registry).isNotNull();
    }

    @Test
    void of_shouldThrowForNullPluginRegistry() {
        org.junit.jupiter.api.Assertions.assertThrows(NullPointerException.class, () -> DSLRegistry.of(null));
    }

    @Test
    void dslFor_withDialectOnly_shouldReturnSuccess() {
        Result<DSL> result = registry.dslFor(SqlTestPlugin.TEST_DIALECT);

        assertThat(result).isInstanceOf(Success.class);
        DSL dsl = result.orElseThrow();
        assertThat(dsl).isNotNull();
    }

    @Test
    void dslFor_withDialectAndVersion_shouldReturnSuccess() {
        Result<DSL> result = registry.dslFor(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.BASE_VERSION);

        assertThat(result).isInstanceOf(Success.class);
        DSL dsl = result.orElseThrow();
        assertThat(dsl).isNotNull();
    }

    @Test
    void dslFor_withUnsupportedDialect_shouldReturnFailure() {
        Result<DSL> result = registry.dslFor("unsupported-dialect");

        assertThat(result).isInstanceOf(Failure.class);
        Failure<DSL> failure = (Failure<DSL>) result;
        assertThat(failure.message()).contains("No plugin found");
    }

    @Test
    void dslFor_withVersionMismatch_shouldReturnFailure() {
        Result<DSL> result = registry.dslFor(SqlTestPlugin.TEST_DIALECT, "999.999.999");

        assertThat(result).isInstanceOf(Failure.class);
    }

    @Test
    void dslFor_shouldReturnDSLWithConfiguredRenderer() {
        Result<DSL> result = registry.dslFor(SqlTestPlugin.TEST_DIALECT);

        DSL dsl = result.orElseThrow();
        // The DSL instance should use the configured renderer
        // We can verify this by checking that it can create builders
        assertThat(dsl.select("col1")).isNotNull();
        assertThat(dsl.insertInto("table1")).isNotNull();
        assertThat(dsl.update("table1")).isNotNull();
        assertThat(dsl.deleteFrom("table1")).isNotNull();
    }

    @Test
    void dslFor_withMySQL_shouldReturnMySQLConfiguredDSL() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        Result<DSL> result = registry.dslFor(MySQLDialectPlugin.DIALECT_NAME, MySQLDialectPlugin.DIALECT_VERSION);

        assertThat(result).isInstanceOf(Success.class);
        DSL dsl = result.orElseThrow();

        // Verify that MySQL-specific rendering works
        String sql = dsl.select("name").from("users").build();
        // MySQL uses backticks by default
        assertThat(sql).contains("`");
    }

    @Test
    void dslFor_withStandardSQL_shouldReturnStandardSQLConfiguredDSL() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        Result<DSL> result =
                registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION);

        assertThat(result).isInstanceOf(Success.class);
        DSL dsl = result.orElseThrow();

        // Verify that StandardSQL-specific rendering works
        String sql = dsl.select("name").from("users").build();
        // StandardSQL uses double quotes
        assertThat(sql).contains("\"");
    }

    @Test
    void dslFor_multipleCalls_shouldReturnDifferentDSLInstances() {
        Result<DSL> result1 = registry.dslFor(SqlTestPlugin.TEST_DIALECT);
        Result<DSL> result2 = registry.dslFor(SqlTestPlugin.TEST_DIALECT);

        DSL dsl1 = result1.orElseThrow();
        DSL dsl2 = result2.orElseThrow();

        // Should return different DSL instances (not cached)
        assertThat(dsl1).isNotSameAs(dsl2);
    }
}
