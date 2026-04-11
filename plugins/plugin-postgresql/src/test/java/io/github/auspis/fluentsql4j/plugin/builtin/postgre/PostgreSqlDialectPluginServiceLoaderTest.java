package io.github.auspis.fluentsql4j.plugin.builtin.postgre;

import static io.github.auspis.fluentsql4j.test.SqlAssert.assertThatSql;
import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.dsl.DSL;
import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.ServiceLoaderBuildHookFactory;
import io.github.auspis.fluentsql4j.hook.build.logging.LoggingBuildHookProvider;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPlugin;
import io.github.auspis.fluentsql4j.plugin.SqlDialectPluginProvider;
import io.github.auspis.fluentsql4j.test.helper.SqlCaptureHelper;
import java.sql.SQLException;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;

class PostgreSqlDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                .anyMatch(PostgreSqlDialectPluginProvider.class::isInstance);

        assertThat(found).isTrue();
    }

    @Test
    void shouldProvideCorrectPluginViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin postgresqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(PostgreSqlDialectPluginProvider.class::isInstance)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        assertThat(postgresqlPlugin).isNotNull();
        assertThat(postgresqlPlugin.dialectName()).isEqualTo("PostgreSQL");
        assertThat(postgresqlPlugin.dialectVersion()).isEqualTo("^15.0.0");
    }

    @Test
    void shouldReturnSingletonInstanceViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin plugin1 = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(PostgreSqlDialectPluginProvider.class::isInstance)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        SqlDialectPlugin plugin2 = PostgreSqlDialectPlugin.instance();

        assertThat(plugin1).isSameAs(plugin2);
    }

    @Test
    void shouldLoadMultiplePluginsIncludingPostgreSQL() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        long count = StreamSupport.stream(serviceLoader.spliterator(), false).count();

        // Should have at least StandardSQL and PostgreSQL plugins
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldProvideWorkingRenderer() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin postgresqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(PostgreSqlDialectPluginProvider.class::isInstance)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElseThrow();

        // Verify the DSL can be created
        assertThat(postgresqlPlugin.createDSL()).isNotNull();
        assertThat(postgresqlPlugin.createDSL().getSpecFactory()).isNotNull();
        assertThat(postgresqlPlugin.createDSL().getSpecFactory().buildHookFactory())
                .isInstanceOf(ServiceLoaderBuildHookFactory.class);
    }

    @Test
    void shouldCreateEnabledBuildHookAndUseItDuringSqlBuild() throws SQLException {
        String originalEnabled = System.getProperty(LoggingBuildHookProvider.ENABLED_PROPERTY);
        System.setProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, "true");

        try {
            DSL dsl = PostgreSqlDialectPlugin.instance().createDSL();

            assertThat(dsl.getSpecFactory().buildHookFactory()).isInstanceOf(ServiceLoaderBuildHookFactory.class);
            assertThat(dsl.getSpecFactory().buildHookFactory().create()).isNotSameAs(BuildHook.nullObject());

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
