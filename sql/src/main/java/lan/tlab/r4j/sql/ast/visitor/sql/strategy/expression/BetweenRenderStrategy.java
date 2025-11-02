package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.predicate.Between;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface BetweenRenderStrategy extends ExpressionRenderStrategy {

    String render(Between expression, SqlRenderer sqlRenderer, AstContext ctx);
}
