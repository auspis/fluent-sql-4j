package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.From;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface FromRenderStrategy extends ClauseRenderStrategy {

    String render(From clause, SqlRenderer sqlRenderer, AstContext ctx);
}
