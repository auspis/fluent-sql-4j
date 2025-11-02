package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Power;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface PowerRenderStrategy extends ExpressionRenderStrategy {

    String render(Power functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
