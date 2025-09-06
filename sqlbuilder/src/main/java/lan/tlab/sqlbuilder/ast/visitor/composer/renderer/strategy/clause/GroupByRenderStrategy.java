package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.groupby.GroupBy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class GroupByRenderStrategy implements ClauseRenderStrategy {

    public String render(GroupBy groupBy, SqlRenderer sqlRenderer) {
        String sql = groupBy.getGroupingExpressions().stream()
                .map(e -> e.accept(sqlRenderer))
                .collect(Collectors.joining(", "));

        if (sql.isBlank()) {
            return "";
        }

        return String.format("GROUP BY %s", sql);
    }
}
