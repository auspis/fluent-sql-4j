package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface ScalarExpressionProjectionRenderStrategy extends ExpressionRenderStrategy {

    String render(ScalarExpressionProjection projection, SqlRenderer sqlRenderer, AstContext ctx);
}
