package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.UnaryString;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class UnaryStringRenderStrategy implements ExpressionRenderStrategy {

    public String render(UnaryString functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "%s(%s)",
                functionCall.getFunctionName(), functionCall.getExpression().accept(sqlRenderer));
    }
}
