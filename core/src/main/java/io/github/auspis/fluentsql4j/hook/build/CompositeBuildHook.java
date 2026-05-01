package io.github.auspis.fluentsql4j.hook.build;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;

public final class CompositeBuildHook extends BuildHook {

    private final List<BuildHook> hooks;

    CompositeBuildHook(List<BuildHook> hooks) {
        this.hooks = List.copyOf(hooks);
    }

    @Override
    protected void doBefore(Statement statement) {
        for (BuildHook hook : hooks) {
            hook.onStart(statement);
        }
    }

    @Override
    protected void doOnSuccess(PreparedStatementSpec spec) {
        for (BuildHook hook : hooks) {
            hook.onSuccess(spec);
        }
    }

    @Override
    protected void doOnError(Throwable error) {
        for (BuildHook hook : hooks) {
            hook.onError(error);
        }
    }
}
