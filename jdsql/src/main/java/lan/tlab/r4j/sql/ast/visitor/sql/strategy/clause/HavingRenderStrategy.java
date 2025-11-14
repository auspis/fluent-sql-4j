package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.dql.clause.Having;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface HavingRenderStrategy extends ClauseRenderStrategy {

    String render(Having clause, SqlRenderer sqlRenderer, AstContext ctx);
}
