package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface SortingRenderStrategy extends ClauseRenderStrategy {

    String render(Sorting sorting, SqlRenderer sqlRenderer, AstContext ctx);
}
