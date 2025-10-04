package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class GroupByRenderStrategy implements ClauseRenderStrategy {

    public String render(GroupBy groupBy, SqlRenderer sqlRenderer, AstContext ctx) {
        String sql = groupBy.getGroupingExpressions().stream()
                .map(e -> e.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));

        if (sql.isBlank()) {
            return "";
        }

        return String.format("GROUP BY %s", sql);
    }
}
