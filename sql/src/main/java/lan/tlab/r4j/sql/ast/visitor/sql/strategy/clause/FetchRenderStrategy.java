package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.dql.clause.Fetch;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface FetchRenderStrategy extends ClauseRenderStrategy {

    String render(Fetch clause, SqlRenderer sqlRenderer, AstContext ctx);
}
