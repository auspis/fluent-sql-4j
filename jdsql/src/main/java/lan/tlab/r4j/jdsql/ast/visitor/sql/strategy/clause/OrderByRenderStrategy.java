package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface OrderByRenderStrategy extends ClauseRenderStrategy {

    String render(OrderBy clause, SqlRenderer sqlRenderer, AstContext ctx);
}
