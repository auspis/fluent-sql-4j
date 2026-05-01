package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class BuildHookInternalErrorLoggingTest {

    private String originalEnabledValue;
    private Logger logger;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setUp() {
        originalEnabledValue = System.getProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY);

        logger = (Logger) LoggerFactory.getLogger(BuildHook.class);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
    }

    @AfterEach
    void tearDown() {
        if (logger != null && appender != null) {
            logger.detachAppender(appender);
            appender.stop();
        }

        if (originalEnabledValue == null) {
            System.clearProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY);
        } else {
            System.setProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY, originalEnabledValue);
        }
    }

    @Test
    void onError_logsInternalExceptionWhenEnabled() {
        System.setProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY, "true");

        BuildHook hook = new BuildHook() {
            @Override
            protected void doOnError(Exception error) {
                throw new IllegalStateException("hook-internal-fail");
            }
        };

        RuntimeException root = new RuntimeException("root-fail");

        assertThatCode(() -> hook.onError(root)).doesNotThrowAnyException();

        assertThat(appender.list).isNotEmpty();
        ILoggingEvent event = appender.list.getLast();

        assertThat(event.getLevel()).isEqualTo(ch.qos.logback.classic.Level.ERROR);
        assertThat(event.getFormattedMessage())
                .isEqualTo("build-hook-internal-error phase=onError hook="
                        + hook.getClass().getName());
        assertThat(event.getThrowableProxy()).isNotNull();
        assertThat(event.getThrowableProxy().getMessage()).isEqualTo("hook-internal-fail");
    }
}
