package io.github.auspis.fluentsql4j.hook.build;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Template hook for build lifecycle around PreparedStatementSpec creation.
 */
public abstract class BuildHook {

    public static final String INTERNAL_ERRORS_ENABLED_PROPERTY = "fluentsql.hooks.build.internal-errors.enabled";

    private static final Logger LOGGER = LoggerFactory.getLogger(BuildHook.class);

    private static final BuildHook NULL_OBJECT_INSTANCE = new BuildHook() {};

    public static BuildHook nullObject() {
        return NULL_OBJECT_INSTANCE;
    }

    public static boolean isNull(BuildHook hook) {
        return hook == null || hook == NULL_OBJECT_INSTANCE;
    }

    public final void onStart(Statement statement) {
        try {
            doBefore(statement);
        } catch (Exception ignored) {
            logInternalError("onStart", ignored);
        }
    }

    public final void onSuccess(PreparedStatementSpec spec) {
        try {
            doOnSuccess(spec);
        } catch (Exception ignored) {
            logInternalError("onSuccess", ignored);
        }
    }

    public final void onError(Throwable error) {
        try {
            doOnError(error);
        } catch (Exception ignored) {
            logInternalError("onError", ignored);
        }
    }

    protected void doBefore(Statement statement) {}

    protected void doOnSuccess(PreparedStatementSpec spec) {}

    protected void doOnError(Throwable error) {}

    static boolean isInternalErrorsLoggingEnabled() {
        return Boolean.parseBoolean(System.getProperty(INTERNAL_ERRORS_ENABLED_PROPERTY, "false"));
    }

    private void logInternalError(String phase, Exception exception) {
        // Hook failures must never break SQL build flow.
        if (!isInternalErrorsLoggingEnabled()) {
            return;
        }

        LOGGER.error(
                "build-hook-internal-error phase={} hook={}", phase, getClass().getName(), exception);
    }
}
