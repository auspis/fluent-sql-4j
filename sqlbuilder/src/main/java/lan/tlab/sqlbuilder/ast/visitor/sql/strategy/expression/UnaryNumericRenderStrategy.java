package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class UnaryNumericRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnaryNumeric functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s(%s)",
                functionCall.getFunctionName(),
                functionCall.getNumericExpression().accept(sqlRenderer, ctx));
    }
}
