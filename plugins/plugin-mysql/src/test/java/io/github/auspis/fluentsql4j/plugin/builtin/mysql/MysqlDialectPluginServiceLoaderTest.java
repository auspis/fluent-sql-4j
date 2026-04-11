package io.github.auspis.fluentsql4j.plugin.builtin.mysql;

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

class MysqlDialectPluginServiceLoaderTest {

    @Test
    void shouldBeDiscoverableViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                .anyMatch(MysqlDialectPluginProvider.class::isInstance);

        assertThat(found).isTrue();
    }

    @Test
    void shouldProvideCorrectPluginViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin mysqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(MysqlDialectPluginProvider.class::isInstance)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        assertThat(mysqlPlugin).isNotNull();
        assertThat(mysqlPlugin.dialectName()).isEqualTo("MySQL");
        assertThat(mysqlPlugin.dialectVersion()).isEqualTo("^8.0.0");
    }

    @Test
    void shouldReturnSingletonInstanceViaServiceLoader() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin plugin1 = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(MysqlDialectPluginProvider.class::isInstance)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElse(null);

        SqlDialectPlugin plugin2 = MysqlDialectPlugin.instance();

        assertThat(plugin1).isSameAs(plugin2);
    }

    @Test
    void shouldLoadMultiplePluginsIncludingMySQL() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        long count = StreamSupport.stream(serviceLoader.spliterator(), false).count();

        // Should have at least StandardSQL and MySQL plugins
        assertThat(count).isGreaterThanOrEqualTo(2);
    }

    @Test
    void shouldProvideWorkingPreparedStatementSpecFactory() {
        ServiceLoader<SqlDialectPluginProvider> serviceLoader = ServiceLoader.load(SqlDialectPluginProvider.class);

        SqlDialectPlugin mysqlPlugin = StreamSupport.stream(serviceLoader.spliterator(), false)
                .filter(MysqlDialectPluginProvider.class::isInstance)
                .map(SqlDialectPluginProvider::get)
                .findFirst()
                .orElseThrow();

        // Verify the DSL can be created
        assertThat(mysqlPlugin.createDSL()).isNotNull();
        assertThat(mysqlPlugin.createDSL().getSpecFactory()).isNotNull();
        assertThat(mysqlPlugin.createDSL().getSpecFactory().buildHookFactory())
                .isInstanceOf(ServiceLoaderBuildHookFactory.class);
    }

    @Test
    void shouldCreateEnabledBuildHookAndUseItDuringSqlBuild() throws SQLException {
        String originalEnabled = System.getProperty(LoggingBuildHookProvider.ENABLED_PROPERTY);
        System.setProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, "true");

        try {
            DSL dsl = MysqlDialectPlugin.instance().createDSL();

            assertThat(dsl.getSpecFactory().buildHookFactory()).isInstanceOf(ServiceLoaderBuildHookFactory.class);
            assertThat(dsl.getSpecFactory().buildHookFactory().create()).isNotSameAs(BuildHook.nullObject());

            SqlCaptureHelper sqlCaptureHelper = new SqlCaptureHelper();
            dsl.select("name").from("users").build(sqlCaptureHelper.getConnection());

            assertThatSql(sqlCaptureHelper)
                    .contains("SELECT")
                    .contains("`name`")
                    .contains("`users`");
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
