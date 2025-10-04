package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class FromSubqueryRenderStrategy implements ClauseRenderStrategy {

    public String render(FromSubquery fromSubquery, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "(%s) %s",
                        fromSubquery.getSubquery().accept(sqlRenderer, ctx),
                        fromSubquery.getAs().accept(sqlRenderer, ctx))
                .trim();
    }
}
