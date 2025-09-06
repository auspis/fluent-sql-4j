package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface CharacterLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(CharacterLength functionCall, SqlRenderer sqlRenderer);

    public static CharacterLengthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer) -> String.format(
                "CHARACTER_LENGTH(%s)", functionCall.getExpression().accept(sqlRenderer));
    }

    public static CharacterLengthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer) -> {
            throw new UnsupportedOperationException("SQL Server does not support CHARACTER_LENGTH funcion call");
        };
    }
}
