package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.CurrentDateTime;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlCurrentDateTimePsStrategyTest {

    @Test
    void returnsCurrentTimestampWithoutParameters() {
        var strategy = new StandardSqlCurrentDateTimePsStrategy();
        var currentDateTime = new CurrentDateTime();
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(currentDateTime, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CURRENT_TIMESTAMP");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCurrentDateTimeFunction() {
        var strategy = new StandardSqlCurrentDateTimePsStrategy();
        var currentDateTime = new CurrentDateTime();
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(currentDateTime, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CURRENT_TIMESTAMP");
        assertThat(result.parameters()).isEmpty();
    }
}
