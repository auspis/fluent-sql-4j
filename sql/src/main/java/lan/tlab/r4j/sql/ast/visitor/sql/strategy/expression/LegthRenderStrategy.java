package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Length;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

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
