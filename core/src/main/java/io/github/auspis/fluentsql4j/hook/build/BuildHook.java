package io.github.auspis.fluentsql4j.hook.build;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

/**
 * Template hook for build lifecycle around PreparedStatementSpec creation.
 */
public abstract class BuildHook {

    private static final BuildHook NULL_OBJECT_INSTANCE = new BuildHook() {};

    public static BuildHook nullObject() {
        return NULL_OBJECT_INSTANCE;
    }

    public final void before(Statement statement) {
        try {
            doBefore(statement);
        } catch (Exception ignored) {
            // Hook failures must never break SQL build flow.
        }
    }

    public final void onSuccess(PreparedStatementSpec spec) {
        try {
            doOnSuccess(spec);
        } catch (Exception ignored) {
            // Hook failures must never break SQL build flow.
        }
    }

    public final void onError(Throwable error) {
        try {
            doOnError(error);
        } catch (Exception ignored) {
            // Never mask original build exception.
        }
    }

    protected void doBefore(Statement statement) {}

    protected void doOnSuccess(PreparedStatementSpec spec) {}

    protected void doOnError(Throwable error) {}
}
