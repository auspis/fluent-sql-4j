package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregationFunctionProjection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class AggregationFunctionProjectionRenderStrategy implements ExpressionRenderStrategy {

    public String render(AggregationFunctionProjection projection, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        projection.getExpression().accept(sqlRenderer, ctx),
                        projection.getAs().accept(sqlRenderer, ctx))
                .trim();
    }
}
