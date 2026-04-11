package io.github.auspis.fluentsql4j.plugin.builtin;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.ServiceLoaderBuildHookFactory;
import io.github.auspis.fluentsql4j.hook.build.logging.LoggingBuildHookProvider;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginRegistry;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.util.List;
import java.util.ServiceLoader;
import org.junit.jupiter.api.Test;

class StandardSQLDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> loader = ServiceLoader.load(SqlDialectPluginProvider.class);

        List<SqlDialectPlugin> plugins = loader.stream()
                .map(ServiceLoader.Provider::get)
                .map(SqlDialectPluginProvider::get)
                .toList();

        // Should find at least the StandardSQLDialectPlugin
        assertThat(plugins).isNotEmpty();

        // Verify StandardSQLDialectPlugin is among discovered plugins
        boolean foundStandardSQL = plugins.stream()
                .anyMatch(p -> "StandardSQL".equals(p.dialectName()) && "2008".equals(p.dialectVersion()));

        assertThat(foundStandardSQL)
                .as("StandardSQLDialectPlugin should be discoverable via ServiceLoader")
                .isTrue();
    }

    @Test
    void shouldBeRegisteredInRegistry() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        assertThat(registry.isSupported("StandardSQL")).isTrue();
        assertThat(registry.isSupported("standardsql")).isTrue(); // case-insensitive
    }

    @Test
    void shouldProvidePreparedStatementSpecFactoryViaRegistry() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory("StandardSQL", "2008");

        assertThat(result).isInstanceOf(Result.Success.class);
        PreparedStatementSpecFactory specFactory = result.orElseThrow();
        assertThat(specFactory).isNotNull();
        assertThat(specFactory.buildHookFactory()).isInstanceOf(ServiceLoaderBuildHookFactory.class);
    }

    @Test
    void shouldProvidePreparedStatementSpecFactoryViaRegistryCaseInsensitive() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Test various case combinations
        Result<PreparedStatementSpecFactory> result1 = registry.getSpecFactory("standardsql", "2008");
        Result<PreparedStatementSpecFactory> result2 = registry.getSpecFactory("STANDARDSQL", "2008");
        Result<PreparedStatementSpecFactory> result3 = registry.getSpecFactory("StandardSQL", "2008");

        assertThat(result1).isInstanceOf(Result.Success.class);
        assertThat(result2).isInstanceOf(Result.Success.class);
        assertThat(result3).isInstanceOf(Result.Success.class);
    }

    @Test
    void shouldUseExactVersionMatching() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // Exact match should work
        Result<PreparedStatementSpecFactory> exactMatch = registry.getSpecFactory("StandardSQL", "2008");
        assertThat(exactMatch).isInstanceOf(Result.Success.class);

        // Different version should fail (non-SemVer uses exact matching)
        Result<PreparedStatementSpecFactory> differentVersion = registry.getSpecFactory("StandardSQL", "2011");
        assertThat(differentVersion).isInstanceOf(Result.Failure.class);
    }

    @Test
    void shouldProvidePreparedStatementSpecFactoryWithoutVersion() {
        SqlDialectPluginRegistry registry = SqlDialectPluginRegistry.createWithServiceLoader();

        // When version is not specified, should return the first available plugin
        Result<PreparedStatementSpecFactory> result = registry.getSpecFactory("StandardSQL");

        assertThat(result).isInstanceOf(Result.Success.class);
        PreparedStatementSpecFactory specFactory = result.orElseThrow();
        assertThat(specFactory).isNotNull();
        assertThat(specFactory.buildHookFactory()).isInstanceOf(ServiceLoaderBuildHookFactory.class);
    }

    @Test
    void shouldCreateEnabledBuildHookAndUseItDuringSqlBuild() throws SQLException {
        String originalEnabled = System.getProperty(LoggingBuildHookProvider.ENABLED_PROPERTY);
        System.setProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, "true");

        try {
            DSL dsl = StandardSQLDialectPlugin.instance().createDSL();
            PreparedStatementSpecFactory specFactory = dsl.getSpecFactory();

            assertThat(specFactory.buildHookFactory()).isInstanceOf(ServiceLoaderBuildHookFactory.class);
            assertThat(specFactory.buildHookFactory().create()).isNotSameAs(BuildHook.nullObject());

            SqlCaptureHelper sqlCaptureHelper = new SqlCaptureHelper();
            dsl.select("name").from("users").build(sqlCaptureHelper.getConnection());

            assertThatSql(sqlCaptureHelper)
                    .contains("SELECT")
                    .contains("\"name\"")
                    .contains("\"users\"");
        } finally {
            restoreProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, originalEnabled);
        }
    }

    private static void restoreProperty(String key, String originalValue) {
        if (originalValue == null) {
            System.clearProperty(key);
            return;
        }
        System.setProperty(key, originalValue);
    }
}
