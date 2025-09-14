package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LiteralRenderStrategyTest {

    private LiteralRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new LiteralRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void string() {
        String sql = strategy.render(Literal.of("string"), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("'string'");
    }

    @Test
    void number() {
        String sql = strategy.render(Literal.of(23), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("23");

        sql = strategy.render(Literal.of(23.023), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("23.023");
    }

    @Test
    void booleanValue() {
        String sql = strategy.render(Literal.of(true), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("true");

        sql = strategy.render(Literal.of(false), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("false");
    }

    @Test
    void nullValue() {
        String sql = strategy.render(Literal.ofNull(), sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("null");
    }
}
