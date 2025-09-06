package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface CharLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(CharLength functionCall, SqlRenderer sqlRenderer);

    public static CharLengthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer) ->
                String.format("CHAR_LENGTH(%s)", functionCall.getExpression().accept(sqlRenderer));
    }

    public static CharLengthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer) -> {
            throw new UnsupportedOperationException("SQL Server does not support CHAR_LENGTH funcion call");
        };
    }
}
