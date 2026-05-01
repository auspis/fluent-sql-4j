package io.github.auspis.fluentsql4j.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import io.github.auspis.fluentsql4j.hook.build.BuildHookFactory;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.StandardSQLDialectPlugin;
import org.junit.jupiter.api.Test;

class DSLRegistryCreateWithHookFactoryTest {

    @Test
    void create_appliesProgrammaticHookFactory() {
        BuildHook[] capturedHook = {null};
        BuildHookFactory factory = () -> {
            capturedHook[0] = BuildHook.nullObject();
            return capturedHook[0];
        };

        DSLRegistry registry = DSLRegistry.create(factory);
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME).orElseThrow();

        assertThat(dsl.getSpecFactory().buildHookFactory()).isSameAs(factory);
    }

    @Test
    void create_cachedDslRetainsProgrammaticHookFactory() {
        BuildHookFactory factory = BuildHook::nullObject;
        DSLRegistry registry = DSLRegistry.create(factory);

        DSL first = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME).orElseThrow();
        DSL second = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME).orElseThrow();

        assertThat(first).isSameAs(second);
        assertThat(first.getSpecFactory().buildHookFactory()).isSameAs(factory);
    }

    @Test
    void create_nullFactoryThrows() {
        assertThatThrownBy(() -> DSLRegistry.create(null)).isInstanceOf(NullPointerException.class);
    }

    @Test
    void createWithServiceLoader_doesNotApplyHookOverride() {
        DSLRegistry registry = DSLRegistry.createWithServiceLoader();
        DSL dsl = registry.dslFor(StandardSQLDialectPlugin.DIALECT_NAME).orElseThrow();

        assertThat(dsl.getSpecFactory().buildHookFactory()).isNotSameAs(BuildHookFactory.nullObject());
    }
}
