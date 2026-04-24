package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.hook.build.logging.LoggingBuildHookProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServiceLoaderBuildHookFactoryIntegrationTest {

    private String originalEnabledValue;
    private ServiceLoaderBuildHookFactory factory;

    @BeforeEach
    void setUp() {
        originalEnabledValue = System.getProperty(LoggingBuildHookProvider.ENABLED_PROPERTY);
        System.clearProperty(LoggingBuildHookProvider.ENABLED_PROPERTY);
        factory = null;
    }

    @AfterEach
    void restoreSystemProperties() {
        if (originalEnabledValue == null) {
            System.clearProperty(LoggingBuildHookProvider.ENABLED_PROPERTY);
        } else {
            System.setProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, originalEnabledValue);
        }
    }

    @Test
    void ok() {
        System.setProperty(LoggingBuildHookProvider.ENABLED_PROPERTY, "true");
        factory = new ServiceLoaderBuildHookFactory();
        BuildHook result = factory.create();
        System.out.println(result.getClass());

        assertThat(result).isNotSameAs(BuildHook.nullObject()).isInstanceOf(CompositeBuildHook.class);
    }

    @Test
    void returnsNullObjectWhenLoggingDisabled() {
        factory = new ServiceLoaderBuildHookFactory();
        BuildHook result = factory.create();
        assertThat(result).isSameAs(BuildHook.nullObject()).isNotInstanceOf(CompositeBuildHook.class);
    }
}
