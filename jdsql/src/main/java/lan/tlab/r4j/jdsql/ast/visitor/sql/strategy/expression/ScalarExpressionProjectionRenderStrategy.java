package lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;

public interface ScalarExpressionProjectionRenderStrategy extends ExpressionRenderStrategy {

    String render(ScalarExpressionProjection projection, SqlRenderer sqlRenderer, AstContext ctx);
}
