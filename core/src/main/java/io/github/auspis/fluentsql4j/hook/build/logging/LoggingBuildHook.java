package io.github.auspis.fluentsql4j.hook.build.logging;

import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.hook.build.BuildHook;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.event.Level;

@EqualsAndHashCode(callSuper = false)
public final class LoggingBuildHook extends BuildHook {

    private final Logger logger;
    private final Level level;
    private final boolean includeParameters;
    private String statementType;

    public LoggingBuildHook(Logger logger, Level level, boolean includeParameters) {
        this.logger = logger;
        this.level = level;
        this.includeParameters = includeParameters;
        this.statementType = "unknown";
    }

    @Override
    protected void doBefore(Statement statement) {
        this.statementType = statement.getClass().getSimpleName();
        log(level, "build-start type={}", statementType);
    }

    @Override
    protected void doOnSuccess(PreparedStatementSpec spec) {
        if (includeParameters) {
            log(level, "build-success type={} sql={} params={}", statementType, spec.sql(), spec.parameters());
            return;
        }
        log(level, "build-success type={} sql={}", statementType, spec.sql());
    }

    @Override
    protected void doOnError(Throwable error) {
        logger.error("build-error type={} message={}", statementType, error.getMessage(), error);
    }

    private void log(Level configuredLevel, String message, Object... args) {
        switch (configuredLevel) {
            case ERROR -> logger.error(message, args);
            case WARN -> logger.warn(message, args);
            case INFO -> logger.info(message, args);
            case TRACE -> logger.trace(message, args);
            case DEBUG -> logger.debug(message, args);
        }
    }
}
