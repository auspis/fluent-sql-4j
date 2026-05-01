package io.github.auspis.fluentsql4j.hook.build;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class CompositeBuildHookTest {

    // ---- inner recording helper ----

    private static class RecordingHook extends BuildHook {
        final List<Statement> beforeCalls = new ArrayList<>();
        final List<PreparedStatementSpec> successCalls = new ArrayList<>();
        final List<Throwable> errorCalls = new ArrayList<>();

        @Override
        protected void doBefore(Statement statement) {
            beforeCalls.add(statement);
        }

        @Override
        protected void doOnSuccess(PreparedStatementSpec spec) {
            successCalls.add(spec);
        }

        @Override
        protected void doOnError(Throwable error) {
            errorCalls.add(error);
        }
    }

    // ---- tests ----

    @Test
    void before_callsAllHooksInOrder() {
        RecordingHook first = new RecordingHook();
        RecordingHook second = new RecordingHook();
        RecordingHook third = new RecordingHook();
        CompositeBuildHook composite = new CompositeBuildHook(List.of(first, second, third));
        Statement statement = Mockito.mock(Statement.class);

        composite.onStart(statement);

        assertThat(first.beforeCalls).containsExactly(statement);
        assertThat(second.beforeCalls).containsExactly(statement);
        assertThat(third.beforeCalls).containsExactly(statement);
    }

    @Test
    void onSuccess_passesSpecToAllHooks() {
        RecordingHook first = new RecordingHook();
        RecordingHook second = new RecordingHook();
        CompositeBuildHook composite = new CompositeBuildHook(List.of(first, second));
        PreparedStatementSpec spec = new PreparedStatementSpec("SELECT 1", List.of());

        composite.onSuccess(spec);

        assertThat(first.successCalls).containsExactly(spec);
        assertThat(second.successCalls).containsExactly(spec);
    }

    @Test
    void onError_callsAllHooks() {
        RecordingHook first = new RecordingHook();
        RecordingHook second = new RecordingHook();
        RecordingHook third = new RecordingHook();
        CompositeBuildHook composite = new CompositeBuildHook(List.of(first, second, third));
        RuntimeException error = new RuntimeException("boom");

        composite.onError(error);

        assertThat(first.errorCalls).containsExactly(error);
        assertThat(second.errorCalls).containsExactly(error);
        assertThat(third.errorCalls).containsExactly(error);
    }

    @Test
    void emptyHooksList_doesNotThrow() {
        CompositeBuildHook composite = new CompositeBuildHook(List.of());
        Statement statement = Mockito.mock(Statement.class);
        PreparedStatementSpec spec = new PreparedStatementSpec("SELECT 1", List.of());
        RuntimeException error = new RuntimeException("boom");

        assertThatCode(() -> composite.onStart(statement)).doesNotThrowAnyException();
        assertThatCode(() -> composite.onSuccess(spec)).doesNotThrowAnyException();
        assertThatCode(() -> composite.onError(error)).doesNotThrowAnyException();
    }

    @Test
    void constructor_defensiveCopy_preventsExternalModification() {
        RecordingHook original = new RecordingHook();
        List<BuildHook> mutableList = new ArrayList<>();
        mutableList.add(original);
        CompositeBuildHook composite = new CompositeBuildHook(mutableList);
        mutableList.add(new RecordingHook());
        Statement statement = Mockito.mock(Statement.class);

        composite.onStart(statement);

        assertThat(original.beforeCalls).hasSize(1);
    }
}
