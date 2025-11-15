package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

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
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlDateArithmeticRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlDateArithmeticRenderStrategyTest {

    private DateArithmeticRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlDateArithmeticRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void add() {
        DateArithmetic exp = DateArithmetic.addition(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"my_table\".\"start_date\" + INTERVAL 7 DAY");
    }

    @Test
    void add_manyIntervals() {
        DateArithmetic exp = DateArithmetic.addition(
                DateArithmetic.addition(
                        ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(1), IntervalUnit.YEAR)),
                new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"my_table\".\"start_date\" + INTERVAL 1 YEAR + INTERVAL 7 DAY");
    }

    @Test
    void subtract() {
        DateArithmetic exp = DateArithmetic.subtraction(
                ColumnReference.of("my_table", "start_date"), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"my_table\".\"start_date\" - INTERVAL 7 DAY");
    }

    @Test
    void subtract_currrentDate() {
        DateArithmetic exp =
                DateArithmetic.subtraction(new CurrentDate(), new Interval(Literal.of(7), IntervalUnit.DAY));
        String sql = strategy.render(exp, renderer, new AstContext());
        assertThat(sql).isEqualTo("CURRENT_DATE() - INTERVAL 7 DAY");
    }

    //    @Test
    //    void dateAdd_negativeInterval() {
    //        DateArithmetic exp = DateArithmetic.dateAdd(
    //        	Literal.of("2023-01-01"),
    //        	Interval.of(Literal.of(-1), IntervalUnit.MONTH));
    //        assertThat(exp.toSQL()).isEqualTo("DATE_ADD('2023-01-01', INTERVAL -1 MONTH)");
    //    }
    //
    //    @Test
    //    void dateAdd_currentDate() {
    //        DateArithmetic exp = DateArithmetic.dateAdd(
    //        	CurrentDate.standardSql(),
    //        	Interval.of(Literal.of(1), IntervalUnit.YEAR));
    //        assertThat(exp.toSQL()).isEqualTo("DATE_ADD(CURRENT_DATE(), INTERVAL 1 YEAR)");
    //    }
    //
    //    @Test
    //    void dateSub_column() {
    //        DateArithmetic func = DateArithmetic.dateSub(
    //        	ColumnReference.of("my_table", "end_date"),
    //        	Interval.of(Literal.of(5), IntervalUnit.HOUR));
    //        assertThat(func.toSQL()).isEqualTo("DATE_SUB(my_table.end_date, INTERVAL 5 HOUR)");
    //    }
    //
    //    @Test
    //    void testDateSubWithLiteralDateAndPositiveInterval() {
    //        DateArithmetic func = DateArithmetic.dateSub(
    //        	Literal.of("2025-12-31"),
    //        	Interval.of(Literal.of(6), IntervalUnit.MONTH));
    //        assertThat(func.toSQL()).isEqualTo("DATE_SUB('2025-12-31', INTERVAL 6 MONTH)");
    //    }

}
