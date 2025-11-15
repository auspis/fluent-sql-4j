package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.GroupBy;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface GroupByRenderStrategy extends ClauseRenderStrategy {

    String render(GroupBy groupBy, SqlRenderer sqlRenderer, AstContext ctx);
}
