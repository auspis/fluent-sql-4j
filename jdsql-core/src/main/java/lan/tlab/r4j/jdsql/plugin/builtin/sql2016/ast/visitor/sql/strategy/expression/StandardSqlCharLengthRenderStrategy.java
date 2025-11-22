package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.CharLength;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.CharLengthRenderStrategy;

public class StandardSqlCharLengthRenderStrategy implements CharLengthRenderStrategy {

    @Override
    public String render(CharLength functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format("CHAR_LENGTH(%s)", functionCall.expression().accept(sqlRenderer, ctx));
    }
}
