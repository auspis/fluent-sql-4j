package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.Interval;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlDateArithmeticPsStrategy;

class StandardSqlDateArithmeticPsStrategyTest {

    @Test
    void handlesDateAddWithLiteral() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(30), Interval.IntervalUnit.DAY);
        var dateArithmetic = DateArithmetic.addition(ColumnReference.of("orders", "created_date"), interval);
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATEADD(INTERVAL ? DAY, \"created_date\")");
        assertThat(result.parameters()).containsExactly(30);
    }

    @Test
    void handlesDateSubtractWithLiteral() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(7), Interval.IntervalUnit.DAY);
        var dateArithmetic = DateArithmetic.subtraction(ColumnReference.of("events", "event_date"), interval);
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATESUB(INTERVAL ? DAY, \"event_date\")");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void handlesDateAddWithMonthInterval() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(3), Interval.IntervalUnit.MONTH);
        var dateArithmetic = DateArithmetic.addition(ColumnReference.of("subscriptions", "start_date"), interval);
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATEADD(INTERVAL ? MONTH, \"start_date\")");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesDateSubtractWithYearInterval() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(1), Interval.IntervalUnit.YEAR);
        var dateArithmetic = DateArithmetic.subtraction(ColumnReference.of("employees", "hire_date"), interval);
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATESUB(INTERVAL ? YEAR, \"hire_date\")");
        assertThat(result.parameters()).containsExactly(1);
    }
}
