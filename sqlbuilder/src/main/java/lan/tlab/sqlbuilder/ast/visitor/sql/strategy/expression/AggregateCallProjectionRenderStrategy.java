package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class AggregateCallProjectionRenderStrategy implements ExpressionRenderStrategy {

    public String render(AggregateCallProjection projection, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        projection.getExpression().accept(sqlRenderer, ctx),
                        projection.getAs().accept(sqlRenderer, ctx))
                .trim();
    }
}
