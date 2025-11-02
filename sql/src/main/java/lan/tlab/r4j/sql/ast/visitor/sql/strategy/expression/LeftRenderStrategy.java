package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Left;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface LeftRenderStrategy extends ExpressionRenderStrategy {

    String render(Left functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
