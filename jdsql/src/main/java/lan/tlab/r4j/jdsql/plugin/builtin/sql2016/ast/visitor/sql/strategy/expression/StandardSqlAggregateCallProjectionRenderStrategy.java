package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.dql.projection.AggregateCallProjection;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.AggregateCallProjectionRenderStrategy;

public class StandardSqlAggregateCallProjectionRenderStrategy implements AggregateCallProjectionRenderStrategy {

    @Override
    public String render(AggregateCallProjection projection, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                        "%s %s",
                        projection.expression().accept(sqlRenderer, ctx),
                        projection.as().accept(sqlRenderer, ctx))
                .trim();
    }
}
