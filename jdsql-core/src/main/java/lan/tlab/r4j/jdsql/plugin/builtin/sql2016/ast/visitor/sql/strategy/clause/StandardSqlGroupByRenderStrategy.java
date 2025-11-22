package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.GroupByRenderStrategy;

public class StandardSqlGroupByRenderStrategy implements GroupByRenderStrategy {

    @Override
    public String render(GroupBy groupBy, SqlRenderer sqlRenderer, AstContext ctx) {
        String sql = groupBy.groupingExpressions().stream()
                .map(e -> e.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));

        if (sql.isBlank()) {
            return "";
        }

        return String.format("GROUP BY %s", sql);
    }
}
