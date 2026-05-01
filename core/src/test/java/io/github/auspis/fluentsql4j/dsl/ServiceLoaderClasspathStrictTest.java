package io.github.auspis.fluentsql4j.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.functional.Result;
import io.github.auspis.fluentsql4j.hook.build.BuildHookProvider;
import io.github.auspis.fluentsql4j.hook.build.logging.LoggingBuildHookProvider;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

@EnabledIfSystemProperty(named = "fluentsql.classpath.strict", matches = "true")
class ServiceLoaderClasspathStrictTest {

    @Test
    void coreModuleRunsUnnamed() {
        assertThat(DSLRegistry.class.getModule().isNamed()).isFalse();
    }

    @Test
    void serviceLoaderResolvesStandardSql() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();

        assertThat(registry.getSupportedDialects()).isNotEmpty();

        Result<DSL> result =
                registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME, StandardSQLDialectPlugin.DIALECT_VERSION);
        assertThat(result.isSuccess()).isTrue();
    }

    @Test
    void serviceLoaderResolvesBuildHookProvider() {
        ServiceLoader<BuildHookProvider> loader = ServiceLoader.load(BuildHookProvider.class);

        boolean found = StreamSupport.stream(loader.spliterator(), false)
                .anyMatch(provider -> provider.getClass().equals(LoggingBuildHookProvider.class));

        assertThat(found).isTrue();
    }
}
