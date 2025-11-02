package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.orderby.OrderBy;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface OrderByRenderStrategy extends ClauseRenderStrategy {

    String render(OrderBy clause, SqlRenderer sqlRenderer, AstContext ctx);
}
