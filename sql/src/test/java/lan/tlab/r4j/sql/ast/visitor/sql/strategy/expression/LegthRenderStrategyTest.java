package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Length;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class LegthRenderStrategyTest {

    @Test
    void standardSql2008() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql2008();
        LegthRenderStrategy strategy = LegthRenderStrategy.standardSql2008();
        Length fun = new Length(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LENGTH(\"Customer\".\"name\")");
    }

    @Test
    void sqlServer() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.sqlServer();
        LegthRenderStrategy strategy = LegthRenderStrategy.sqlServer();
        Length fun = new Length(ColumnReference.of("Customer", "name"));
        String sql = strategy.render(fun, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LEN([Customer].[name])");
    }
}
