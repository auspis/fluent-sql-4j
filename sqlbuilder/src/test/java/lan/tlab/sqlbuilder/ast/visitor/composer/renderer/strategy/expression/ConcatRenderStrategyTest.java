package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConcatRenderStrategyTest {

    private ConcatRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = ConcatRenderStrategy.standardSql2008();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void literal() {
        Concat concat = Concat.concat(Literal.of("first"), Literal.of("second"), Literal.of("third"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("('first' || 'second' || 'third')");
    }

    @Test
    void columnReference() {
        Concat concat = Concat.concat(Literal.of("Mr."), ColumnReference.of("Customer", "name"));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("('Mr.' || \"Customer\".\"name\")");
    }

    @Test
    void functionCall() {
        Concat concat = Concat.concat(Literal.of("Mr."), Substring.of(ColumnReference.of("Customer", "name"), 1, 5));
        String sql = strategy.render(concat, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("('Mr.' || SUBSTRING(\"Customer\".\"name\", 1, 5))");
    }
}
