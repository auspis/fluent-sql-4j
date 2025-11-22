package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.dialect.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlSqlRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateTimeRenderStrategyTest {

    @Test
    void mysql() {
        SqlRenderer sqlRenderer = MysqlSqlRendererFactory.create();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("NOW()");
    }
}
