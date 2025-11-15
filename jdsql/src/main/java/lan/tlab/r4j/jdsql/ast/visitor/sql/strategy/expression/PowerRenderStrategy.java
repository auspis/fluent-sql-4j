package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number.Power;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface PowerRenderStrategy extends ExpressionRenderStrategy {

    String render(Power functionCall, SqlRenderer sqlRenderer, AstContext ctx);
}
