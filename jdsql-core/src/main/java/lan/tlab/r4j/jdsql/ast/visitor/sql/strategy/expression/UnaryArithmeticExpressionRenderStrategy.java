package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface UnaryArithmeticExpressionRenderStrategy extends ExpressionRenderStrategy {

    String render(UnaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx);
}
