package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.date.arithmetic;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval.IntervalUnit;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.DateArithmeticRenderStrategy;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateArithmeticSqlServerRenderStrategyTest {

    private DateArithmeticRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = DateArithmeticRenderStrategy.sqlServer();
        renderer = TestDialectRendererFactory.sqlServer();
    }

    @Test
    void add() {
        DateArithmetic exp = DateArithmetic.addition(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATEADD(DAY, 7, [my_table].[start_date])");
    }

    @Test
    void add_manyIntervals() {
        DateArithmetic exp = DateArithmetic.addition(
                DateArithmetic.addition(
                        ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(1), IntervalUnit.YEAR)),
                new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATEADD(DAY, 7, DATEADD(YEAR, 1, [my_table].[start_date]))");
    }

    @Test
    void subtract() {
        DateArithmetic exp = DateArithmetic.subtraction(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATEADD(DAY, -7, [my_table].[start_date])");
    }

    @Test
    void subtract_currrentDate() {
        DateArithmetic exp =
                DateArithmetic.subtraction(new CurrentDate(), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATEADD(DAY, -7, CURRENT_DATE())");
    }
}
