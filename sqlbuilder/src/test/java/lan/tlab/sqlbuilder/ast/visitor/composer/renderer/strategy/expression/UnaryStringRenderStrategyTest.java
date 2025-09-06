package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.UnaryString;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UnaryStringRenderStrategyTest {

    private UnaryStringRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new UnaryStringRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void lower_columnReference() {
        UnaryString func = UnaryString.lower(ColumnReference.of("Customer", "value"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("LOWER(\"Customer\".\"value\")");
    }

    @Test
    void lower_literal() {
        UnaryString func = UnaryString.lower(Literal.of("ABC"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("LOWER('ABC')");
    }

    @Test
    void lower_arithmeticExpression() {
        UnaryString func = UnaryString.lower(Substring.of(ColumnReference.of("Customer", "name"), 2));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("LOWER(SUBSTRING(\"Customer\".\"name\", 2))");
    }
}
