package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.clause.orderby.OrderBy;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class OrderByRenderStrategy implements ClauseRenderStrategy {

    public String render(OrderBy clause, SqlRenderer sqlRenderer, AstContext ctx) {
        String sql = clause.getSortings().stream()
                .map(s -> s.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));
        if (sql.isBlank()) {
            return "";
        }
        return String.format("ORDER BY %s", sql);
    }
}
