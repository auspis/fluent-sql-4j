package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.interval.Interval.IntervalUnit;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.DateArithmeticRenderStrategy;
import lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression.MysqlDateArithmeticRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlDateArithmeticRenderStrategyTest {

    private DateArithmeticRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new MysqlDateArithmeticRenderStrategy();
        renderer = TestDialectRendererFactory.mysql();
    }

    @Test
    void add() {
        DateArithmetic exp = DateArithmetic.addition(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATE_ADD(`my_table`.`start_date`, INTERVAL 7 DAY)");
    }

    @Test
    void add_manyIntervals() {
        DateArithmetic exp = DateArithmetic.addition(
                DateArithmetic.addition(
                        ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(1), IntervalUnit.YEAR)),
                new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATE_ADD(DATE_ADD(`my_table`.`start_date`, INTERVAL 1 YEAR), INTERVAL 7 DAY)");
    }

    @Test
    void subtract() {
        DateArithmetic exp = DateArithmetic.subtraction(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATE_SUB(`my_table`.`start_date`, INTERVAL 7 DAY)");
    }

    @Test
    void subtract_currrentDate() {
        DateArithmetic exp =
                DateArithmetic.subtraction(new CurrentDate(), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("DATE_SUB(CURDATE(), INTERVAL 7 DAY)");
    }
}
