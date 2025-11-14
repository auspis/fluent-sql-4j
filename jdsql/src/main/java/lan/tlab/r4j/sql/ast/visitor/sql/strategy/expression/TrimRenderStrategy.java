package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Trim;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface TrimRenderStrategy extends ExpressionRenderStrategy {

    String render(Trim functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
