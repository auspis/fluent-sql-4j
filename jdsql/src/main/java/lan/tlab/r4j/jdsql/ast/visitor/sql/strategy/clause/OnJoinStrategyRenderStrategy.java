package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface OnJoinStrategyRenderStrategy extends ClauseRenderStrategy {

    String render(OnJoin onJoin, SqlRenderer sqlRenderer, AstContext ctx);
}
