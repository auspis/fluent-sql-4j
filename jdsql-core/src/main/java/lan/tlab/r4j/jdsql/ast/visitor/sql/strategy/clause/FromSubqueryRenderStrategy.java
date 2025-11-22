package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.source.FromSubquery;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface FromSubqueryRenderStrategy extends ClauseRenderStrategy {

    String render(FromSubquery fromSubquery, SqlRenderer sqlRenderer, AstContext ctx);
}
