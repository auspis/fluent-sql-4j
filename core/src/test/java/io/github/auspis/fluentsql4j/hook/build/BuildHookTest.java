package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuildHookTest {

    private String originalInternalErrorsEnabled;

    @BeforeEach
    void setUp() {
        originalInternalErrorsEnabled = System.getProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY);
        System.clearProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY);
    }

    @AfterEach
    void tearDown() {
        if (originalInternalErrorsEnabled == null) {
            System.clearProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY);
            return;
        }
        System.setProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY, originalInternalErrorsEnabled);
    }

    @Test
    void before() {
        BuildHook hook = new BuildHook() {
            @Override
            protected void doBefore(Statement statement) {
                throw new IllegalStateException("before-fail");
            }
        };

        Statement statement = Mockito.mock(Statement.class);

        assertThatCode(() -> hook.onStart(statement)).doesNotThrowAnyException();
    }

    @Test
    void onSuccess() {
        BuildHook hook = new BuildHook() {
            @Override
            protected void doOnSuccess(PreparedStatementSpec spec) {
                throw new IllegalStateException("success-fail");
            }
        };

        PreparedStatementSpec spec = new PreparedStatementSpec("SELECT 1", List.of());

        assertThatCode(() -> hook.onSuccess(spec)).doesNotThrowAnyException();
    }

    @Test
    void onError() {
        BuildHook hook = new BuildHook() {
            @Override
            protected void doOnError(Throwable error) {
                throw new IllegalStateException("error-fail");
            }
        };

        RuntimeException root = new RuntimeException("root");

        assertThatCode(() -> hook.onError(root)).doesNotThrowAnyException();
    }

    @Test
    void isNull() {
        assertThat(BuildHook.isNull(BuildHook.nullObject())).isTrue();
        assertThat(BuildHook.isNull(null)).isTrue();
    }

    @Test
    void internalErrorsLoggingDisabledByDefault() {
        assertThat(BuildHook.isInternalErrorsLoggingEnabled()).isFalse();
    }

    @Test
    void internalErrorsLoggingEnabledWhenPropertyIsTrue() {
        System.setProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY, "true");

        assertThat(BuildHook.isInternalErrorsLoggingEnabled()).isTrue();
    }

    @Test
    void internalErrorsLoggingDisabledWhenPropertyIsInvalid() {
        System.setProperty(BuildHook.INTERNAL_ERRORS_ENABLED_PROPERTY, "not-a-boolean");

        assertThat(BuildHook.isInternalErrorsLoggingEnabled()).isFalse();
    }
}
