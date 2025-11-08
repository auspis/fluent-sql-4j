package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface AggregateCallProjectionRenderStrategy extends ExpressionRenderStrategy {

    String render(AggregateCallProjection projection, SqlRenderer sqlRenderer, AstContext ctx);
}
