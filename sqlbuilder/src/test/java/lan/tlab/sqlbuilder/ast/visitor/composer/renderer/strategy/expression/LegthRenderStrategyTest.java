package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Length;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.Test;

class LegthRenderStrategyTest {

    @Test
    void standardSql2008() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.standardSql2008();
        LegthRenderStrategy strategy = LegthRenderStrategy.standardSql2008();
        Length fun = new Length(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LENGTH(\"Customer\".\"name\")");
    }

    @Test
    void sqlServer() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.sqlServer();
        LegthRenderStrategy strategy = LegthRenderStrategy.sqlServer();
        Length fun = new Length(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEN([Customer].[name])");
    }
}
