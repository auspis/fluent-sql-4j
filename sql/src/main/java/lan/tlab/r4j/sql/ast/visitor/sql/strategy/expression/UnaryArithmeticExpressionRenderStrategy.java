package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.UnaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class UnaryArithmeticExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s%s)", expression.operator(), expression.expression().accept(sqlRenderer, ctx));
    }
}
