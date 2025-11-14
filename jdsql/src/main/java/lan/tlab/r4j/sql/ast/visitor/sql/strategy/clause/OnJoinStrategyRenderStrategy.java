package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.dql.source.join.OnJoin;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface OnJoinStrategyRenderStrategy extends ClauseRenderStrategy {

    String render(OnJoin onJoin, SqlRenderer sqlRenderer, AstContext ctx);
}
