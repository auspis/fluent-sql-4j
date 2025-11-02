package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CharacterLengthRenderStrategy;

public class StandardSqlCharacterLengthRenderStrategy implements CharacterLengthRenderStrategy {

    @Override
    public String render(CharacterLength functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("CHARACTER_LENGTH(%s)", functionCall.expression().accept(sqlRenderer, ctx));
    }
}
