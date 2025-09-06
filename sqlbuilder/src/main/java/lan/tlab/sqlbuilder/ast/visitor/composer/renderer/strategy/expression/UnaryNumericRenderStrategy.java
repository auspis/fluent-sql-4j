package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class UnaryNumericRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnaryNumeric functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "%s(%s)",
                functionCall.getFunctionName(),
                functionCall.getNumericExpression().accept(sqlRenderer));
    }
}
