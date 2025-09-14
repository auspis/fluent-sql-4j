package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Left;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Length;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LeftRenderStrategyTest {

    private LeftRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new LeftRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        Left func = Left.of(ColumnReference.of("Customer", "name"), 3);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEFT(\"Customer\".\"name\", 3)");
    }

    @Test
    void withLiteralStringAndColumnLength() {
        Left func = Left.of(Literal.of("Some String"), ColumnReference.of("Customer", "prefix_length"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEFT('Some String', \"Customer\".\"prefix_length\")");
    }

    @Test
    void withArithmeticExpressionAsLength() {
        Left func = Left.of(
                ColumnReference.of("Customer", "full_name"),
                ArithmeticExpression.subtraction(
                        new Length(ColumnReference.of("Customer", "full_name")), Literal.of(5)));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEFT(\"Customer\".\"full_name\", (LENGTH(\"Customer\".\"full_name\") - 5))");
    }
}
