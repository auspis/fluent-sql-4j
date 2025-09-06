package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateRenderStrategyTest {

    @Test
    void standardSql() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.standardSql2008();
        String sql = sqlRenderer.visit(new CurrentDate());
        assertThat(sql).isEqualTo("CURRENT_DATE()");
    }

    @Test
    void mysql() {
        SqlRendererImpl sqlRenderer = SqlRendererFactory.mysql();
        String sql = sqlRenderer.visit(new CurrentDate());
        assertThat(sql).isEqualTo("CURDATE()");
    }
}
