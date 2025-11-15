package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.predicate.Between;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface BetweenRenderStrategy extends ExpressionRenderStrategy {

    String render(Between expression, SqlRenderer sqlRenderer, AstContext ctx);
}
