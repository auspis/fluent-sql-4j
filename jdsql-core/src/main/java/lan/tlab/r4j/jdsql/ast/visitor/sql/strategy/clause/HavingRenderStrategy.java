package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.Having;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface HavingRenderStrategy extends ClauseRenderStrategy {

    String render(Having clause, SqlRenderer sqlRenderer, AstContext ctx);
}
