package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface WhereRenderStrategy extends ClauseRenderStrategy {

    String render(Where clause, SqlRenderer sqlRenderer, AstContext ctx);
}
