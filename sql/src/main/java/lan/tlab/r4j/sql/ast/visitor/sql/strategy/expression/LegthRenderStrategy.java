package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Length;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface LegthRenderStrategy extends ExpressionRenderStrategy {

    String render(Length functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
