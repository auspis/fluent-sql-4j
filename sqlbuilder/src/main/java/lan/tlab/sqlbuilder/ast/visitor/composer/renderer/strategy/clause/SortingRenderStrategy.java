package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class SortingRenderStrategy implements ClauseRenderStrategy {

    public String render(Sorting sorting, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        sorting.getExpression().accept(sqlRenderer, ctx),
                        sorting.getSortOrder().getSqlKeyword())
                .strip();
    }
}
