package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.clause.selection.projection.AggregateCallProjection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlAggregateCallProjectionRenderStrategy implements ExpressionRenderStrategy {

    public String render(AggregateCallProjection projection, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        projection.expression().accept(sqlRenderer, ctx),
                        projection.as().accept(sqlRenderer, ctx))
                .trim();
    }
}
