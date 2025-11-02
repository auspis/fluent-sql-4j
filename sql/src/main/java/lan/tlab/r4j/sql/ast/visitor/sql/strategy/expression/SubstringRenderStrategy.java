package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Substring;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface SubstringRenderStrategy extends ExpressionRenderStrategy {

    String render(Substring functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
