package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.from.From;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface FromRenderStrategy extends ClauseRenderStrategy {

    String render(From clause, SqlRenderer sqlRenderer, AstContext ctx);
}
