package lan.tlab.r4j.jdsql.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import lan.tlab.r4j.jdsql.ast.visitor.DialectRenderer;
import lan.tlab.r4j.jdsql.functional.Result;
import lan.tlab.r4j.jdsql.functional.Result.Failure;
import lan.tlab.r4j.jdsql.functional.Result.Success;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPlugin;
import lan.tlab.r4j.jdsql.plugin.SqlDialectPluginRegistry;
import lan.tlab.r4j.jdsql.plugin.SqlTestPlugin;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSQLDialectPlugin;
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
        assertThrows(NullPointerException.class, () -> DSLRegistry.of(null));
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
    void dslFor_withStandardSQL_shouldReturnStandardSQLConfiguredDSL() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        Result<DSL> result =
                registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION);

        assertThat(result).isInstanceOf(Success.class);
        result.orElseThrow();
    }

    @Test
    void dslFor_multipleCalls_shouldReturnSameCachedDSLInstance() {
        Result<DSL> result1 = registry.dslFor(SqlTestPlugin.TEST_DIALECT);
        Result<DSL> result2 = registry.dslFor(SqlTestPlugin.TEST_DIALECT);

        DSL dsl1 = result1.orElseThrow();
        DSL dsl2 = result2.orElseThrow();

        // Should return the same cached DSL instance (Registry pattern)
        assertThat(dsl1).isSameAs(dsl2);
    }

    @Test
    void dslFor_withDifferentVersions_shouldReturnDifferentInstances() {
        // Setup two plugins with different versions
        DialectRenderer renderer1 = mock(DialectRenderer.class);
        DialectRenderer renderer2 = mock(DialectRenderer.class);
        SqlDialectPlugin plugin1 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^3.0.0", renderer1);
        SqlDialectPlugin plugin2 = SqlTestPlugin.create(SqlTestPlugin.TEST_DIALECT, "^1.7.0", renderer2);
        SqlDialectPluginRegistry multiVersionRegistry =
                SqlDialectPluginRegistry.of(java.util.List.of(plugin1, plugin2));
        DSLRegistry multiRegistry = DSLRegistry.of(multiVersionRegistry);

        Result<DSL> result1 = multiRegistry.dslFor(SqlTestPlugin.TEST_DIALECT, "3.0.0");
        Result<DSL> result2 = multiRegistry.dslFor(SqlTestPlugin.TEST_DIALECT, "1.7.0");

        DSL dsl1 = result1.orElseThrow();
        DSL dsl2 = result2.orElseThrow();

        // Different versions should have different cached instances
        assertThat(dsl1).isNotSameAs(dsl2);
    }

    @Test
    void getSupportedDialects_shouldReturnAllDialects() {
        assertThat(registry.getSupportedDialects()).contains(SqlTestPlugin.TEST_DIALECT.toLowerCase());
    }

    @Test
    void isSupported_withSupportedDialect_shouldReturnTrue() {
        assertThat(registry.isSupported(SqlTestPlugin.TEST_DIALECT)).isTrue();
    }

    @Test
    void isSupported_withUnsupportedDialect_shouldReturnFalse() {
        assertThat(registry.isSupported("unsupported-dialect")).isFalse();
    }

    @Test
    void clearCache_shouldRemoveAllCachedInstances() {
        // Create some cached instances
        registry.dslFor(SqlTestPlugin.TEST_DIALECT).orElseThrow();
        assertThat(registry.getCacheSize()).isEqualTo(1);

        // Clear the cache
        registry.clearCache();

        assertThat(registry.getCacheSize()).isZero();

        // After clearing, should create new instance
        DSL newDsl = registry.dslFor(SqlTestPlugin.TEST_DIALECT).orElseThrow();
        assertThat(newDsl).isNotNull();
        assertThat(registry.getCacheSize()).isEqualTo(1);
    }

    @Test
    void getCacheSize_shouldReturnCorrectSize() {
        assertThat(registry.getCacheSize()).isZero();

        registry.dslFor(SqlTestPlugin.TEST_DIALECT).orElseThrow();
        assertThat(registry.getCacheSize()).isEqualTo(1);

        registry.dslFor(SqlTestPlugin.TEST_DIALECT, SqlTestPlugin.BASE_VERSION).orElseThrow();
        assertThat(registry.getCacheSize()).isEqualTo(2);
    }

    @Test
    void cacheKey_shouldBeCaseInsensitive() {
        DSL dsl1 = registry.dslFor(SqlTestPlugin.TEST_DIALECT).orElseThrow();
        DSL dsl2 = registry.dslFor(SqlTestPlugin.TEST_DIALECT.toLowerCase()).orElseThrow();
        DSL dsl3 = registry.dslFor(SqlTestPlugin.TEST_DIALECT.toUpperCase()).orElseThrow();

        // All should return the same cached instance
        assertThat(dsl1).isSameAs(dsl2).isSameAs(dsl3);
        assertThat(registry.getCacheSize()).isEqualTo(1);
    }
}
