package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.number.Power;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlPowerRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlPowerRenderStrategyTest {

    private StandardSqlPowerRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlPowerRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Power func = Power.of(2, 3);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("POWER(2, 3)");
    }

    @Test
    void baseColumnBased() {
        Power func = Power.of(ColumnReference.of("my_table", "base_value"), 2);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("POWER(\"my_table\".\"base_value\", 2)");
    }

    @Test
    void exponentColumnBased() {
        Power func = Power.of(10, ColumnReference.of("my_table", "power_level"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("POWER(10, \"my_table\".\"power_level\")");
    }

    @Test
    void testPowerWithArithmeticExpressions() {
        Power func = Power.of(
                ArithmeticExpression.addition(ColumnReference.of("my_table", "x"), Literal.of(1)),
                ArithmeticExpression.multiplication(ColumnReference.of("my_table", "y"), Literal.of(2)));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("POWER((\"my_table\".\"x\" + 1), (\"my_table\".\"y\" * 2))");
    }
}
