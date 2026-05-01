package io.github.auspis.fluentsql4j.hook.build.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import java.util.Properties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

class LoggingBuildHookProviderTest {

    private final LoggingBuildHookProvider provider = new LoggingBuildHookProvider();

    @Test
    void id() {
        assertThat(provider.id()).isEqualTo("fluentsql.hooks.build.logging");
    }

    @Test
    void order() {
        assertThat(provider.order()).isEqualTo(1000);
    }

    @Test
    void disabledByDefault() {
        assertThat(provider.isEnabled()).isFalse();
    }

    @Test
    void configure_enablesProvider() {
        Properties properties = new Properties();
        properties.setProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, "true");

        provider.configure(properties);

        assertThat(provider.isEnabled()).isTrue();
    }

    @Test
    void configure_disablesProvider() {
        Properties properties = new Properties();
        properties.setProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, "false");

        provider.configure(properties);

        assertThat(provider.isEnabled()).isFalse();
    }

    @Test
    void createDefaults() {
        LoggingBuildHook expected =
                new LoggingBuildHook(LoggerFactory.getLogger(LoggingBuildHook.class), Level.DEBUG, false);

        BuildHook hook = provider.create();

        assertThat(hook).isNotNull().isInstanceOf(LoggingBuildHook.class).isEqualTo(expected);
        assertThatCode(() -> hook.onSuccess(new io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec(
                        "SELECT 1", java.util.List.of())))
                .doesNotThrowAnyException();
    }

    @Test
    void createWithIncludesParams() {
        Properties properties = new Properties();
        properties.setProperty(LoggingBuildHookProvider.INCLUDE_PARAMS_PROPERTY, "true");
        provider.configure(properties);

        LoggingBuildHook expected =
                new LoggingBuildHook(LoggerFactory.getLogger(LoggingBuildHook.class), Level.DEBUG, true);

        BuildHook hook = provider.create();

        assertThat(hook).isNotNull().isInstanceOf(LoggingBuildHook.class).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"ERROR", "error", "WARN", "warn", "INFO", "info", "TRACE", "trace", "DEBUG", "debug"})
    void create_acceptsAllValidLevels(String level) {
        Properties properties = new Properties();
        properties.setProperty(LoggingBuildHookProvider.LEVEL_PROPERTY, level);
        provider.configure(properties);

        BuildHook hook = provider.create();

        assertThat(hook)
                .isNotNull()
                .isInstanceOf(LoggingBuildHook.class)
                .extracting("level")
                .isEqualTo(Level.valueOf(level.toUpperCase()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"INVALID", "xyz", "", "  "})
    void create_fallsBackToDebug_forInvalidLevel(String level) {
        Properties properties = new Properties();
        properties.setProperty(LoggingBuildHookProvider.LEVEL_PROPERTY, level);
        provider.configure(properties);

        BuildHook hook = provider.create();

        assertThat(hook)
                .isNotNull()
                .isInstanceOf(LoggingBuildHook.class)
                .extracting("level")
                .isEqualTo(Level.DEBUG);
    }

    @Test
    void programmaticConstructor_enabledAndConfigured() {
        Logger logger = LoggerFactory.getLogger("test");
        LoggingBuildHookProvider configured = new LoggingBuildHookProvider(logger, Level.INFO, true, true);

        assertThat(configured.isEnabled()).isTrue();
        assertThat(configured.create()).isInstanceOf(LoggingBuildHook.class);
    }
}
