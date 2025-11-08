package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.CharLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CharLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(CharLength functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
