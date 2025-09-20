package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModRenderStrategyTest {

    private ModRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new ModRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        Mod func = Mod.of(10, 3);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MOD(10, 3)");
    }

    @Test
    void dividendColumnBased() {
        Mod func = Mod.of(ColumnReference.of("my_table", "base_value"), 2);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MOD(\"my_table\".\"base_value\", 2)");
    }

    @Test
    void divisorColumnBased() {
        Mod func = Mod.of(10, ColumnReference.of("my_table", "power_level"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MOD(10, \"my_table\".\"power_level\")");
    }

    @Test
    void testModWithArithmeticExpressions() {
        Mod func = Mod.of(
                ArithmeticExpression.addition(ColumnReference.of("my_table", "x"), Literal.of(1)),
                ArithmeticExpression.multiplication(ColumnReference.of("my_table", "y"), Literal.of(2)));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MOD((\"my_table\".\"x\" + 1), (\"my_table\".\"y\" * 2))");
    }
}
