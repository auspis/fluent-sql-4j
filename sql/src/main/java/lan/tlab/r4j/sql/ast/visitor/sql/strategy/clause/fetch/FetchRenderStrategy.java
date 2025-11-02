package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.fetch;

import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.ClauseRenderStrategy;
import lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.sql.strategy.clasue.fetch.MySqlLimitOffsetRenderStrategy;

public interface FetchRenderStrategy extends ClauseRenderStrategy {

    String render(Fetch clause, SqlRenderer sqlRenderer, AstContext ctx);

    public static FetchRenderStrategy standardSql2008() {
        return new OffsetRowsRenderStrategy();
    }

    public static FetchRenderStrategy mysql() {
        return new MySqlLimitOffsetRenderStrategy();
    }
}
