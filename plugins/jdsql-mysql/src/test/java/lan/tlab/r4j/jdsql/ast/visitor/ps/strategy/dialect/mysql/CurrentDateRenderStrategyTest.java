package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.dialect.mysql;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlSqlRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateRenderStrategyTest {

    @Test
    void mysql() {
        SqlRenderer sqlRenderer = MysqlSqlRendererFactory.create();
        String sql = sqlRenderer.visit(new CurrentDate(), new AstContext());
        assertThat(sql).isEqualTo("CURDATE()");
    }
}
