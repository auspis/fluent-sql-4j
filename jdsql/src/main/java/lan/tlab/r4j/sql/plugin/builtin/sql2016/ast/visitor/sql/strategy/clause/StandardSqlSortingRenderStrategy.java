package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.SortingRenderStrategy;

public class StandardSqlSortingRenderStrategy implements SortingRenderStrategy {

    @Override
    public String render(Sorting sorting, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        sorting.expression().accept(sqlRenderer, ctx),
                        sorting.sortOrder().getSqlKeyword())
                .strip();
    }
}
