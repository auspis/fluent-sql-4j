package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Length;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface LegthRenderStrategy extends ExpressionRenderStrategy {

    String render(Length functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static LegthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) ->
                String.format("LENGTH(%s)", functionCall.getExpression().accept(sqlRenderer, ctx));
    }

    static LegthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) ->
                String.format("LEN(%s)", functionCall.getExpression().accept(sqlRenderer, ctx));
    }
}
