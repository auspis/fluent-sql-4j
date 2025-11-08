package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface NullSetExpressionRenderStrategy extends ExpressionRenderStrategy {

    String render(NullSetExpression expression, SqlRenderer sqlRenderer, AstContext ctx);
}
