package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.jdsql.ast.dql.clause.Sorting;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface SortingRenderStrategy extends ClauseRenderStrategy {

    String render(Sorting sorting, SqlRenderer sqlRenderer, AstContext ctx);
}
