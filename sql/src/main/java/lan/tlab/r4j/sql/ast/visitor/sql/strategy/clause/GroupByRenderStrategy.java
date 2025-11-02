package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.clause.groupby.GroupBy;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface GroupByRenderStrategy extends ClauseRenderStrategy {

    String render(GroupBy groupBy, SqlRenderer sqlRenderer, AstContext ctx);
}
