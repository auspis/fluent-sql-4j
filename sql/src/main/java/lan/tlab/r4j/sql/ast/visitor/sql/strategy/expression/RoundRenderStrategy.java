package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.number.Round;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface RoundRenderStrategy extends ExpressionRenderStrategy {

    String render(Round functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
