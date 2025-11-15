package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause.OrderByRenderStrategy;

public class StandardSqlOrderByRenderStrategy implements OrderByRenderStrategy {

    @Override
    public String render(OrderBy clause, SqlRenderer sqlRenderer, AstContext ctx) {
        String sql =
                clause.sortings().stream().map(s -> s.accept(sqlRenderer, ctx)).collect(Collectors.joining(", "));
        if (sql.isBlank()) {
            return "";
        }
        return String.format("ORDER BY %s", sql);
    }
}
