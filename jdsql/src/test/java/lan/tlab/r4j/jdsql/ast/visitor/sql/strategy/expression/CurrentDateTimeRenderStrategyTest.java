package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.oracle.OracleSqlRendererFactory;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.Test;

class CurrentDateTimeRenderStrategyTest {

    @Test
    void standardSql() {
        SqlRenderer sqlRenderer = StandardSqlRendererFactory.standardSql();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("CURRENT_TIMESTAMP()");
    }

    @Test
    void oracle() {
        SqlRenderer sqlRenderer = OracleSqlRendererFactory.oracle();
        String sql = sqlRenderer.visit(new CurrentDateTime(), new AstContext());
        assertThat(sql).isEqualTo("SYSDATE()");
    }
}
