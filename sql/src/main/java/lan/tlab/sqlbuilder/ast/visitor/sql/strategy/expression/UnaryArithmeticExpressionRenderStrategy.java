package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class UnaryArithmeticExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s%s)", expression.getOperator(), expression.getExpression().accept(sqlRenderer, ctx));
    }
}
