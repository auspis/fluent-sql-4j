package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ReplaceRenderStrategy implements ExpressionRenderStrategy {

    public String render(Replace functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "REPLACE(%s, %s, %s)",
                functionCall.getExpression().accept(sqlRenderer),
                functionCall.getOldSubstring().accept(sqlRenderer),
                functionCall.getNewSubstring().accept(sqlRenderer));
    }
}
