package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ReplaceRenderStrategy implements ExpressionRenderStrategy {

    public String render(Replace functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "REPLACE(%s, %s, %s)",
                functionCall.getExpression().accept(sqlRenderer, ctx),
                functionCall.getOldSubstring().accept(sqlRenderer, ctx),
                functionCall.getNewSubstring().accept(sqlRenderer, ctx));
    }
}
