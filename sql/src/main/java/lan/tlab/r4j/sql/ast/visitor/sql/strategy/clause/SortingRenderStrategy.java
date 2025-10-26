package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.orderby.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class SortingRenderStrategy implements ClauseRenderStrategy {

    public String render(Sorting sorting, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        sorting.expression().accept(sqlRenderer, ctx),
                        sorting.sortOrder().getSqlKeyword())
                .strip();
    }
}
