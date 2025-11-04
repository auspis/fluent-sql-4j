package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface BinaryArithmeticExpressionRenderStrategy extends ExpressionRenderStrategy {

    String render(BinaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx);
}
