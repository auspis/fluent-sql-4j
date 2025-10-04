package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CharLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(CharLength functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static CharLengthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) ->
                String.format("CHAR_LENGTH(%s)", functionCall.getExpression().accept(sqlRenderer, ctx));
    }

    public static CharLengthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) -> {
            throw new UnsupportedOperationException("SQL Server does not support CHAR_LENGTH funcion call");
        };
    }
}
