package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface AggregateCallRenderStrategy extends ExpressionRenderStrategy {

    String render(AggregateCall expression, SqlRenderer sqlRenderer, AstContext ctx);
}
