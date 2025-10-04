package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultDateArithmeticPsStrategyTest {

    @Test
    void handlesDateAddWithLiteral() {
        var strategy = new DefaultDateArithmeticPsStrategy();
        var interval = Interval.of(Literal.of(30), Interval.IntervalUnit.DAY);
        var dateArithmetic = DateArithmetic.add(ColumnReference.of("orders", "created_date"), interval);
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATEADD(INTERVAL ? DAY, \"created_date\")");
        assertThat(result.parameters()).containsExactly(30);
    }

    @Test
    void handlesDateSubtractWithLiteral() {
        var strategy = new DefaultDateArithmeticPsStrategy();
        var interval = Interval.of(Literal.of(7), Interval.IntervalUnit.DAY);
        var dateArithmetic = DateArithmetic.subtract(ColumnReference.of("events", "event_date"), interval);
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATESUB(INTERVAL ? DAY, \"event_date\")");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void handlesDateAddWithMonthInterval() {
        var strategy = new DefaultDateArithmeticPsStrategy();
        var interval = Interval.of(Literal.of(3), Interval.IntervalUnit.MONTH);
        var dateArithmetic = DateArithmetic.add(ColumnReference.of("subscriptions", "start_date"), interval);
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATEADD(INTERVAL ? MONTH, \"start_date\")");
        assertThat(result.parameters()).containsExactly(3);
    }

    @Test
    void handlesDateSubtractWithYearInterval() {
        var strategy = new DefaultDateArithmeticPsStrategy();
        var interval = Interval.of(Literal.of(1), Interval.IntervalUnit.YEAR);
        var dateArithmetic = DateArithmetic.subtract(ColumnReference.of("employees", "hire_date"), interval);
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(dateArithmetic, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATESUB(INTERVAL ? YEAR, \"hire_date\")");
        assertThat(result.parameters()).containsExactly(1);
    }
}
