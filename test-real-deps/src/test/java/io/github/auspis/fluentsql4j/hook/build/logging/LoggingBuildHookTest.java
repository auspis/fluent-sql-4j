package io.github.auspis.fluentsql4j.hook.build.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.core.statement.Statement;
import io.github.auspis.fluentsql4j.ast.dql.clause.From;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement;
import io.github.auspis.fluentsql4j.ast.dql.statement.SelectStatement.SelectStatementBuilder;
import io.github.auspis.fluentsql4j.ast.visitor.PreparedStatementSpecFactory;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

class LoggingBuildHookTest {

    private Logger logger;
    private ListAppender<ILoggingEvent> appender;
    private SelectStatementBuilder selectStatementBuilder;
    private PreparedStatementSpecFactory preparedStatementSpecFactory =
            new PreparedStatementSpecFactory(new AstToPreparedStatementSpecVisitor());

    @BeforeEach
    void setUp() {
        selectStatementBuilder = SelectStatement.builder().from(From.fromTable("users"));
    }

    @AfterEach
    void tearDown() {
        if (logger != null && appender != null) {
            logger.detachAppender(appender);
            appender.stop();
        }
    }

    @Test
    void ok() {
        SelectStatement statement = selectStatementBuilder.build();
        PreparedStatementSpec spec = preparedStatementSpecFactory.create(statement);
        LoggingBuildHook hook = new LoggingBuildHook(attachInMemoryLogger(), Level.DEBUG, false);

        hook.onStart(statement);
        hook.onSuccess(spec);

        assertThat(appender.list).hasSize(2);
        ILoggingEvent startEvent = appender.list.get(0);
        ILoggingEvent successEvent = appender.list.get(1);

        assertThat(startEvent.getLevel()).isEqualTo(ch.qos.logback.classic.Level.DEBUG);
        assertThat(startEvent.getFormattedMessage()).isEqualTo("build-start type=SelectStatement");

        assertThat(successEvent.getLevel()).isEqualTo(ch.qos.logback.classic.Level.DEBUG);
        assertThat(successEvent.getFormattedMessage())
                .isEqualTo("build-success type=SelectStatement sql=" + spec.sql());
    }

    @Test
    void withParams() {
        Statement statement = selectStatementBuilder
                .where(Where.of(Comparison.eq(ColumnReference.of("users", "id"), Literal.of(7))))
                .build();
        PreparedStatementSpec spec = preparedStatementSpecFactory.create(statement);
        LoggingBuildHook hook = new LoggingBuildHook(attachInMemoryLogger(), Level.DEBUG, true);

        hook.onStart(statement);
        hook.onSuccess(spec);

        assertThat(appender.list).hasSize(2);
        ILoggingEvent successEvent = appender.list.get(1);

        assertThat(successEvent.getLevel()).isEqualTo(ch.qos.logback.classic.Level.DEBUG);
        assertThat(successEvent.getFormattedMessage())
                .isEqualTo("build-success type=SelectStatement sql=" + spec.sql() + " params=" + spec.parameters());
        assertThat(spec.parameters()).containsExactly(7);
    }

    @Test
    void logsErrorWithThrowable() {
        LoggingBuildHook hook = new LoggingBuildHook(attachInMemoryLogger(), Level.DEBUG, false);
        RuntimeException error = new RuntimeException("boom");

        hook.onStart(selectStatementBuilder.build());
        hook.onError(error);

        assertThat(appender.list).hasSize(2);
        ILoggingEvent errorEvent = appender.list.get(1);

        assertThat(errorEvent.getLevel()).isEqualTo(ch.qos.logback.classic.Level.ERROR);
        assertThat(errorEvent.getFormattedMessage()).isEqualTo("build-error type=SelectStatement message=boom");
        assertThat(errorEvent.getThrowableProxy()).isNotNull();
        assertThat(errorEvent.getThrowableProxy().getMessage()).isEqualTo("boom");
    }

    @Test
    void doesNotPropagateLoggerFailure() {
        org.slf4j.Logger slf4jLogger = mock(org.slf4j.Logger.class);
        doThrow(new RuntimeException("logger-fail"))
                .when(slf4jLogger)
                .debug(anyString(), org.mockito.ArgumentMatchers.<Object[]>any());
        LoggingBuildHook hook = new LoggingBuildHook(slf4jLogger, Level.DEBUG, false);

        assertThatCode(() -> hook.onStart(selectStatementBuilder.build())).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(Level.class)
    void logDispatchesToCorrectLogLevel(Level level) {
        SelectStatement statement = selectStatementBuilder.build();
        LoggingBuildHook hook = new LoggingBuildHook(attachInMemoryLogger(), level, false);

        hook.onStart(statement);

        assertThat(appender.list).hasSize(1);
        ch.qos.logback.classic.Level expectedLogbackLevel = ch.qos.logback.classic.Level.toLevel(level.name());
        assertThat(appender.list.get(0).getLevel()).isEqualTo(expectedLogbackLevel);
    }

    @Test
    void onSuccess_usesUnknownStatementType_whenBeforeNotCalled() {
        PreparedStatementSpec spec = new PreparedStatementSpec("", List.of());
        LoggingBuildHook hook = new LoggingBuildHook(attachInMemoryLogger(), Level.DEBUG, false);

        hook.onSuccess(spec);

        assertThat(appender.list).hasSize(1);
        assertThat(appender.list.get(0).getFormattedMessage()).contains("type=unknown");
    }

    private Logger attachInMemoryLogger() {
        logger = (Logger) LoggerFactory.getLogger("test.logging.buildhook." + UUID.randomUUID());
        logger.setAdditive(false);
        logger.setLevel(ch.qos.logback.classic.Level.TRACE);
        appender = new ListAppender<>();
        appender.start();
        logger.addAppender(appender);
        return logger;
    }
}
