package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.Interval;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlIntervalPsStrategyTest {

    @Test
    void handlesIntervalWithLiteral() {
        var strategy = new StandardSqlIntervalPsStrategy();
        var interval = new Interval(Literal.of(30), Interval.IntervalUnit.DAY);
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(interval, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INTERVAL ? DAY");
        assertThat(result.parameters()).containsExactly(30);
    }
}
