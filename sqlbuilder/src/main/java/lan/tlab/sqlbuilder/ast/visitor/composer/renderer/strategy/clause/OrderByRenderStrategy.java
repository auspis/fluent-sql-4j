package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.clause.orderby.OrderBy;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class OrderByRenderStrategy implements ClauseRenderStrategy {

    public String render(OrderBy clause, SqlRenderer sqlRenderer) {
        String sql =
                clause.getSortings().stream().map(s -> s.accept(sqlRenderer)).collect(Collectors.joining(", "));
        if (sql.isBlank()) {
            return "";
        }
        return String.format("ORDER BY %s", sql);
    }
}
