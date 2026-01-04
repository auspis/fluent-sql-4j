package io.github.massimiliano.fluentsql4j.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.type;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import io.github.massimiliano.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.massimiliano.fluentsql4j.functional.Result;
import io.github.massimiliano.fluentsql4j.functional.Result.Failure;
import io.github.massimiliano.fluentsql4j.functional.Result.Success;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.massimiliano.fluentsql4j.plugin.SqlDialectPluginRegistry;
import io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import io.github.massimiliano.fluentsql4j.plugin.util.SqlDialectPluginUtil;
import io.github.massimiliano.fluentsql4j.plugin.util.TestDialectDSL;
import io.github.massimiliano.fluentsql4j.plugin.util.TestDialectPlugin;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DSLRegistryTest {

    private PreparedStatementSpecFactory preparedStatementSpecFactory;
    private SqlDialectPluginRegistry pluginRegistry;
    private DSLRegistry registry;

    @BeforeEach
    void setUp() {
        preparedStatementSpecFactory = mock(PreparedStatementSpecFactory.class);
        pluginRegistry = SqlDialectPluginRegistry.of(
                List.of(TestDialectPlugin.instance(), SqlDialectPluginUtil.create(preparedStatementSpecFactory)));
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
        Result<DSL> result = registry.dslFor(TestDialectPlugin.DIALECT_NAME);

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void dslFor_withDialectAndVersion_shouldReturnSuccess() {
        Result<DSL> result = registry.dslFor(TestDialectPlugin.DIALECT_NAME, TestDialectPlugin.DIALECT_VERSION);

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void dslFor_withUnsupportedDialect_shouldReturnFailure() {
        Result<DSL> result = registry.dslFor("unsupported-dialect");

        assertThat(result).isInstanceOf(Failure.class);
        Failure<DSL> failure = (Failure<DSL>) result;
        assertThat(failure.message()).contains("No plugin found");
    }

    @Test
    void dslFor_withDialectAndPluginClass_shouldReturnSuccess() {
        Result<DSL> result = registry.dslFor(TestDialectPlugin.DIALECT_NAME, DSL.class);

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void dslFor_withDialectAndVersionAndPluginClass_shouldReturnSuccess() {
        Result<TestDialectDSL> result = registry.dslFor(
                TestDialectPlugin.DIALECT_NAME, TestDialectPlugin.DIALECT_VERSION, TestDialectDSL.class);

        assertThat(result).isInstanceOf(Success.class);
        assertThat(result.orElseThrow()).isNotNull();
    }

    @Test
    void dslFor_withDialectAndVersionAndUnsupportedPluginClass__shouldReturnFailure() {
        Result<TestDialectDSL> result = registry.dslFor(
                StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION, TestDialectDSL.class);

        assertThat(result)
                .isInstanceOf(Failure.class)
                .asInstanceOf(type(Failure.class))
                .satisfies(failure -> assertThat(failure.message()).contains("No plugin found"));
    }

    @Test
    void dslFor_withVersionMismatch_shouldReturnFailure() {
        Result<DSL> result = registry.dslFor(TestDialectPlugin.DIALECT_NAME, "999.999.999");

        assertThat(result).isInstanceOf(Failure.class);
    }

    @Test
    void dslFor_shouldReturnDSLWithConfiguredPreparedStatementSpecFactory() {
        Result<DSL> result = registry.dslFor(TestDialectPlugin.DIALECT_NAME);

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
        Result<DSL> result1 = registry.dslFor(TestDialectPlugin.DIALECT_NAME);
        Result<DSL> result2 = registry.dslFor(TestDialectPlugin.DIALECT_NAME);

        DSL dsl1 = result1.orElseThrow();
        DSL dsl2 = result2.orElseThrow();

        // Should return the same cached DSL instance (Registry pattern)
        assertThat(dsl1).isSameAs(dsl2);
    }

    @Test
    void dslFor_withDifferentVersions_shouldReturnDifferentInstances() {
        // Setup two plugins with different versions
        PreparedStatementSpecFactory specFactory1 = mock(PreparedStatementSpecFactory.class);
        PreparedStatementSpecFactory specFactory2 = mock(PreparedStatementSpecFactory.class);
        SqlDialectPlugin plugin1 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^3.0.0", specFactory1);
        SqlDialectPlugin plugin2 = SqlDialectPluginUtil.create(TestDialectPlugin.DIALECT_NAME, "^1.7.0", specFactory2);
        SqlDialectPluginRegistry multiVersionRegistry =
                SqlDialectPluginRegistry.of(java.util.List.of(plugin1, plugin2));
        DSLRegistry multiRegistry = DSLRegistry.of(multiVersionRegistry);

        Result<DSL> result1 = multiRegistry.dslFor(TestDialectPlugin.DIALECT_NAME, "3.0.0");
        Result<DSL> result2 = multiRegistry.dslFor(TestDialectPlugin.DIALECT_NAME, "1.7.0");

        DSL dsl1 = result1.orElseThrow();
        DSL dsl2 = result2.orElseThrow();

        // Different versions should have different cached instances
        assertThat(dsl1).isNotSameAs(dsl2);
    }

    @Test
    void getSupportedDialects_shouldReturnAllDialects() {
        assertThat(registry.getSupportedDialects()).contains(TestDialectPlugin.DIALECT_NAME.toLowerCase());
    }

    @Test
    void isSupported_withSupportedDialect_shouldReturnTrue() {
        assertThat(registry.isSupported(TestDialectPlugin.DIALECT_NAME)).isTrue();
    }

    @Test
    void isSupported_withUnsupportedDialect_shouldReturnFalse() {
        assertThat(registry.isSupported("unsupported-dialect")).isFalse();
    }

    @Test
    void clearCache_shouldRemoveAllCachedInstances() {
        // Create some cached instances
        registry.dslFor(TestDialectPlugin.DIALECT_NAME).orElseThrow();
        assertThat(registry.getCacheSize()).isEqualTo(1);

        // Clear the cache
        registry.clearCache();

        assertThat(registry.getCacheSize()).isZero();

        // After clearing, should create new instance
        DSL newDsl = registry.dslFor(TestDialectPlugin.DIALECT_NAME).orElseThrow();
        assertThat(newDsl).isNotNull();
        assertThat(registry.getCacheSize()).isEqualTo(1);
    }

    @Test
    void getCacheSize_shouldReturnCorrectSize() {
        assertThat(registry.getCacheSize()).isZero();

        registry.dslFor(TestDialectPlugin.DIALECT_NAME).orElseThrow();
        assertThat(registry.getCacheSize()).isEqualTo(1);

        registry.dslFor(TestDialectPlugin.DIALECT_NAME, TestDialectPlugin.DIALECT_VERSION)
                .orElseThrow();
        assertThat(registry.getCacheSize()).isEqualTo(2);
    }

    @Test
    void cacheKey_shouldBeCaseInsensitive() {
        DSL dsl1 = registry.dslFor(TestDialectPlugin.DIALECT_NAME).orElseThrow();
        DSL dsl2 = registry.dslFor(TestDialectPlugin.DIALECT_NAME.toLowerCase()).orElseThrow();
        DSL dsl3 = registry.dslFor(TestDialectPlugin.DIALECT_NAME.toUpperCase()).orElseThrow();

        // All should return the same cached instance
        assertThat(dsl1).isSameAs(dsl2).isSameAs(dsl3);
        assertThat(registry.getCacheSize()).isEqualTo(1);
    }
}
