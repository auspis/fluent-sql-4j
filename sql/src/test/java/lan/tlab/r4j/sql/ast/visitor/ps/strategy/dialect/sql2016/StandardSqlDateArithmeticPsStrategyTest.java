package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class StandardSqlDateArithmeticPsStrategyTest {

    @Test
    void handlesDateAddWithLiteral() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(30), Interval.IntervalUnit.DAY);
        var dateArithmetic = DateArithmetic.addition(ColumnReference.of("orders", "created_date"), interval);
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATEADD(INTERVAL ? DAY, \"created_date\")");
        assertThat(result.parameters()).containsExactly(30);
    }

    @Test
    void handlesDateSubtractWithLiteral() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(7), Interval.IntervalUnit.DAY);
        var dateArithmetic = DateArithmetic.subtraction(ColumnReference.of("events", "event_date"), interval);
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATESUB(INTERVAL ? DAY, \"event_date\")");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void handlesDateAddWithMonthInterval() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(3), Interval.IntervalUnit.MONTH);
        var dateArithmetic = DateArithmetic.addition(ColumnReference.of("subscriptions", "start_date"), interval);
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATEADD(INTERVAL ? MONTH, \"start_date\")");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesDateSubtractWithYearInterval() {
        var strategy = new StandardSqlDateArithmeticPsStrategy();
        var interval = new Interval(Literal.of(1), Interval.IntervalUnit.YEAR);
        var dateArithmetic = DateArithmetic.subtraction(ColumnReference.of("employees", "hire_date"), interval);
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATESUB(INTERVAL ? YEAR, \"hire_date\")");
        assertThat(result.parameters()).containsExactly(1);
    }
}
