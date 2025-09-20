package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateTimeRenderStrategyTest {

    @Test
    void standardSql() {
        SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("CURRENT_TIMESTAMP()");
    }

    @Test
    void mysql() {
        SqlRenderer sqlRenderer = SqlRendererFactory.mysql();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("NOW()");
    }

    @Test
    void sqlServer() {
        SqlRenderer sqlRenderer = SqlRendererFactory.sqlServer();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("GETDATE()");
    }

    @Test
    void oracle() {
        SqlRenderer sqlRenderer = SqlRendererFactory.oracle();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("SYSDATE()");
    }
}
