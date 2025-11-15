package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateRenderStrategyTest {

    @Test
    void standardSql() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql();
        String sql = sqlRenderer.visit(new CurrentDate(), new AstContext());
        assertThat(sql).isEqualTo("CURRENT_DATE()");
    }

    @Test
    void mysql() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.mysql();
        String sql = sqlRenderer.visit(new CurrentDate(), new AstContext());
        assertThat(sql).isEqualTo("CURDATE()");
    }
}
