package io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDate;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.DateArithmetic;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.Interval;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.Interval.IntervalUnit;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.plugin.builtin.mysql.MysqlAstToPreparedStatementSpecVisitorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlDateArithmeticRenderStrategyTest {

    private MysqlDateArithmeticRenderStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;

    @BeforeEach
    public void setUp() {
        strategy = new MysqlDateArithmeticRenderStrategy();
        specFactory = MysqlAstToPreparedStatementSpecVisitorFactory.create();
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
