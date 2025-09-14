package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class BinaryArithmeticExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(BinaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s %s %s)",
                expression.getLhs().accept(sqlRenderer, ctx),
                expression.getOperator(),
                expression.getRhs().accept(sqlRenderer, ctx));
    }
}
