package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CastRenderStrategy extends ExpressionRenderStrategy {

    String render(Cast functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
