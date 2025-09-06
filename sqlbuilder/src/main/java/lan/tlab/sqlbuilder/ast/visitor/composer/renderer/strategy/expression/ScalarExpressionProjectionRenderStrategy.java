package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ScalarExpressionProjectionRenderStrategy implements ExpressionRenderStrategy {

    public String render(ScalarExpressionProjection projection, SqlRenderer sqlRenderer) {
        return String.format(
                        "%s %s",
                        projection.getExpression().accept(sqlRenderer),
                        projection.getAs().accept(sqlRenderer))
                .trim();
    }
}
