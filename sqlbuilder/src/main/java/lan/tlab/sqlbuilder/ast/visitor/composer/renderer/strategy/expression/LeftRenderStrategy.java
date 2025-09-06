package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Left;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class LeftRenderStrategy implements ExpressionRenderStrategy {

    public String render(Left functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "LEFT(%s, %s)",
                functionCall.getExpression().accept(sqlRenderer),
                functionCall.getLength().accept(sqlRenderer));
    }
}
