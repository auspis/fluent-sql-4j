package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.set.IntersectExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface IntersectRenderStrategy extends ExpressionRenderStrategy {

    String render(IntersectExpression expression, SqlRenderer sqlRenderer, AstContext ctx);
}
