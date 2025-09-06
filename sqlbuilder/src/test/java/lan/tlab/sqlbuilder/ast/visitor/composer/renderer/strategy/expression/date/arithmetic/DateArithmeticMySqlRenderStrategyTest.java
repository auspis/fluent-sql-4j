package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.date.arithmetic;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.DateArithmetic;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval.Interval.IntervalUnit;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.DateArithmeticRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DateArithmeticMySqlRenderStrategyTest {

    private DateArithmeticRenderStrategy strategy;
    private SqlRendererImpl renderer;

    @BeforeEach
    public void setUp() {
        strategy = DateArithmeticRenderStrategy.mysql();
        renderer = SqlRendererFactory.mysql();
    }

    @Test
    void add() {
        DateArithmetic exp = DateArithmetic.add(
                ColumnReference.of("my_table", "start_date"), Interval.of(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer);
        assertThat(sql).isEqualTo("DATE_ADD(`my_table`.`start_date`, INTERVAL 7 DAY)");
    }

    @Test
    void add_manyIntervals() {
        DateArithmetic exp = DateArithmetic.add(
                DateArithmetic.add(
                        ColumnReference.of("my_table", "start_date"), Interval.of(Literal.of(1), IntervalUnit.YEAR)),
                Interval.of(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer);
        assertThat(sql).isEqualTo("DATE_ADD(DATE_ADD(`my_table`.`start_date`, INTERVAL 1 YEAR), INTERVAL 7 DAY)");
    }

    @Test
    void subtract() {
        DateArithmetic exp = DateArithmetic.subtract(
                ColumnReference.of("my_table", "start_date"), Interval.of(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer);
        assertThat(sql).isEqualTo("DATE_SUB(`my_table`.`start_date`, INTERVAL 7 DAY)");
    }

    @Test
    void subtract_currrentDate() {
        DateArithmetic exp = DateArithmetic.subtract(new CurrentDate(), Interval.of(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer);
        assertThat(sql).isEqualTo("DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
    }
}
