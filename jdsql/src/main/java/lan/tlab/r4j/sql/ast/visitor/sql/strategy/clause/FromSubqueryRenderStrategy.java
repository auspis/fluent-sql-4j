package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface FromSubqueryRenderStrategy extends ClauseRenderStrategy {

    String render(FromSubquery fromSubquery, SqlRenderer sqlRenderer, AstContext ctx);
}
