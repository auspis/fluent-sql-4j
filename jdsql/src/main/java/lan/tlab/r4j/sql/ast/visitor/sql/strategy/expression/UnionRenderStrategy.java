package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.set.UnionExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface UnionRenderStrategy extends ExpressionRenderStrategy {

    String render(UnionExpression expression, SqlRenderer sqlRenderer, AstContext ctx);
}
