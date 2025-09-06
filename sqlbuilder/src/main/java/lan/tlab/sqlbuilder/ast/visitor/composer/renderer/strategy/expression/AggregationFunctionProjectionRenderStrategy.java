package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class AggregationFunctionProjectionRenderStrategy implements ExpressionRenderStrategy {

    public String render(AggregationFunctionProjection projection, SqlRenderer sqlRenderer) {
        return String.format(
                        "%s %s",
                        projection.getExpression().accept(sqlRenderer),
                        projection.getAs().accept(sqlRenderer))
                .trim();
    }
}
