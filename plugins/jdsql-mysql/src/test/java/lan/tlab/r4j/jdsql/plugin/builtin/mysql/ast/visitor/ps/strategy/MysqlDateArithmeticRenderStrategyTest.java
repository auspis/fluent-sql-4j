package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.DateArithmetic;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.interval.Interval.IntervalUnit;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlPreparedStatementRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlDateArithmeticRenderStrategyTest {

    private MysqlDateArithmeticRenderStrategy strategy;
    private PreparedStatementRenderer specFactory;

    @BeforeEach
    public void setUp() {
        strategy = new MysqlDateArithmeticRenderStrategy();
        specFactory = MysqlPreparedStatementRendererFactory.create();
    }

    @Test
    void add() {
        DateArithmetic exp = DateArithmetic.addition(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        PreparedStatementSpec result = strategy.handle(exp, specFactory, new AstContext());
        assertThat(result.sql()).isEqualTo("DATE_ADD(`start_date`, INTERVAL ? DAY)");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void add_manyIntervals() {
        DateArithmetic exp = DateArithmetic.addition(
                DateArithmetic.addition(
                        ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(1), IntervalUnit.YEAR)),
                new Interval(Literal.of(7), IntervalUnit.DAY));
        PreparedStatementSpec result = strategy.handle(exp, specFactory, new AstContext());
        assertThat(result.sql()).isEqualTo("DATE_ADD(DATE_ADD(`start_date`, INTERVAL ? YEAR), INTERVAL ? DAY)");
        assertThat(result.parameters()).containsExactly(1, 7);
    }

    @Test
    void subtract() {
        DateArithmetic exp = DateArithmetic.subtraction(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        PreparedStatementSpec result = strategy.handle(exp, specFactory, new AstContext());
        assertThat(result.sql()).isEqualTo("DATE_SUB(`start_date`, INTERVAL ? DAY)");
        assertThat(result.parameters()).containsExactly(7);
    }

    @Test
    void subtract_currrentDate() {
        DateArithmetic exp =
                DateArithmetic.subtraction(new CurrentDate(), new Interval(Literal.of(7), IntervalUnit.DAY));
        PreparedStatementSpec result = strategy.handle(exp, specFactory, new AstContext());
        assertThat(result.sql()).isEqualTo("DATE_SUB(CURDATE(), INTERVAL ? DAY)");
        assertThat(result.parameters()).containsExactly(7);
    }
}
