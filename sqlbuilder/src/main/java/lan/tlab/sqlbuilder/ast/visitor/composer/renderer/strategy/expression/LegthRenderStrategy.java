package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Length;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface LegthRenderStrategy extends ExpressionRenderStrategy {

    String render(Length functionCall, SqlRenderer sqlRenderer);

    public static LegthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer) ->
                String.format("LENGTH(%s)", functionCall.getExpression().accept(sqlRenderer));
    }

    static LegthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer) ->
                String.format("LEN(%s)", functionCall.getExpression().accept(sqlRenderer));
    }
}
