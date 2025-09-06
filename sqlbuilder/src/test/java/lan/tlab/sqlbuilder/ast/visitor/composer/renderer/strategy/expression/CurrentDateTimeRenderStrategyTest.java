package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateTimeRenderStrategyTest {

    @Test
    void standardSql() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.standardSql2008();
        String sql = sqlRenderer.visit(new CurrentDateTime());
        assertThat(sql).isEqualTo("CURRENT_TIMESTAMP()");
    }

    @Test
    void mysql() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.mysql();
        String sql = sqlRenderer.visit(new CurrentDateTime());
        assertThat(sql).isEqualTo("NOW()");
    }

    @Test
    void sqlServer() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.sqlServer();
        String sql = sqlRenderer.visit(new CurrentDateTime());
        assertThat(sql).isEqualTo("GETDATE()");
    }

    @Test
    void oracle() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.oracle();
        String sql = sqlRenderer.visit(new CurrentDateTime());
        assertThat(sql).isEqualTo("SYSDATE()");
    }
}
