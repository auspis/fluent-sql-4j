package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateRenderStrategyTest {

    @Test
    void standardSql() {
        SqlRenderer sqlRenderer = SqlRendererFactory.standardSql2008();
        String sql = sqlRenderer.visit(new CurrentDate(), new AstContext());
        assertThat(sql).isEqualTo("CURRENT_DATE()");
    }

    @Test
    void mysql() {
        SqlRenderer sqlRenderer = SqlRendererFactory.mysql();
        String sql = sqlRenderer.visit(new CurrentDate(), new AstContext());
        assertThat(sql).isEqualTo("CURDATE()");
    }
}
