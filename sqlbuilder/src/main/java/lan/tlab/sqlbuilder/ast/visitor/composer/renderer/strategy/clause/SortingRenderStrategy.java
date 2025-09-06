package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class SortingRenderStrategy implements ClauseRenderStrategy {

    public String render(Sorting sorting, SqlRenderer sqlRenderer) {
        return String.format(
                        "%s %s",
                        sorting.getExpression().accept(sqlRenderer),
                        sorting.getSortOrder().getSqlKeyword())
                .strip();
    }
}
