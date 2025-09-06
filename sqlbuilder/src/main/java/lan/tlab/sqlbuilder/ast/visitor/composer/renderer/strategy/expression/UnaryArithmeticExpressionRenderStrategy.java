package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class UnaryArithmeticExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnaryArithmeticExpression expression, SqlRenderer sqlRenderer) {
        return String.format(
                "(%s%s)", expression.getOperator(), expression.getExpression().accept(sqlRenderer));
    }
}
