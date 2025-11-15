package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.Select;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface SelectRenderStrategy extends ClauseRenderStrategy {

    String render(Select clause, SqlRenderer sqlRenderer, AstContext ctx);
}
