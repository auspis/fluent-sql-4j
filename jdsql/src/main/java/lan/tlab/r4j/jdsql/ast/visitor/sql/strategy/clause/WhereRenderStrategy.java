package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.Where;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface WhereRenderStrategy extends ClauseRenderStrategy {

    String render(Where clause, SqlRenderer sqlRenderer, AstContext ctx);
}
