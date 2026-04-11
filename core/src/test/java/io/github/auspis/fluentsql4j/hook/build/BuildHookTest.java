package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class BuildHookTest {

    @Test
    void before() {
        BuildHook hook = new BuildHook() {
            @Override
            protected void doBefore(Statement statement) {
                throw new IllegalStateException("before-fail");
            }
        };

        Statement statement = Mockito.mock(Statement.class);

        assertThatCode(() -> hook.before(statement)).doesNotThrowAnyException();
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
}
