package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.FromSubqueryRenderStrategy;

public class StandardSqlFromSubqueryRenderStrategy implements FromSubqueryRenderStrategy {

    @Override
    public String render(FromSubquery fromSubquery, SqlRenderer sqlRenderer, AstContext ctx) {
        return fromSubquery.aliased().accept(sqlRenderer, ctx);
    }
}
