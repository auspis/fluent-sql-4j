package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import lan.tlab.sqlbuilder.ast.clause.from.source.FromSubquery;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class FromSubqueryRenderStrategy implements ClauseRenderStrategy {

    public String render(FromSubquery fromSubquery, SqlRenderer sqlRenderer) {
        return String.format(
                        "(%s) %s",
                        fromSubquery.getSubquery().accept(sqlRenderer),
                        fromSubquery.getAs().accept(sqlRenderer))
                .trim();
    }
}
