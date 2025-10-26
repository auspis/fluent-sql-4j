package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression.BinaryArithmeticExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class BinaryArithmeticExpressionRenderStrategy implements ExpressionRenderStrategy {

    public String render(BinaryArithmeticExpression expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s %s %s)",
                expression.lhs().accept(sqlRenderer, ctx),
                expression.operator(),
                expression.rhs().accept(sqlRenderer, ctx));
    }
}
