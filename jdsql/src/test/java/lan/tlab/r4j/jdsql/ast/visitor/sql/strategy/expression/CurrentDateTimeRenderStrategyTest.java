package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateTimeRenderStrategyTest {

    @Test
    void standardSql() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.standardSql();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("CURRENT_TIMESTAMP()");
    }

    @Test
    void mysql() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.mysql();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("NOW()");
    }

    @Test
    void oracle() {
        SqlRenderer sqlRenderer = TestDialectRendererFactory.oracle();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("SYSDATE()");
    }
}
