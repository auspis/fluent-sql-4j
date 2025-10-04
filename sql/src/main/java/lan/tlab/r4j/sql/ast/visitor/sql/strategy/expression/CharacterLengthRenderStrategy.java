package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CharacterLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(CharacterLength functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static CharacterLengthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "CHARACTER_LENGTH(%s)", functionCall.getExpression().accept(sqlRenderer, ctx));
    }

    public static CharacterLengthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) -> {
            throw new UnsupportedOperationException("SQL Server does not support CHARACTER_LENGTH funcion call");
        };
    }
}
