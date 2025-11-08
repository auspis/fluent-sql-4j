package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.CharacterLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CharacterLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(CharacterLength functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
