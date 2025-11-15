package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCallImpl;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.CountDistinct;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.CountStar;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.AggregateCallRenderStrategy;

public class StandardSqlAggregateCallRenderStrategy implements AggregateCallRenderStrategy {

    @Override
    public String render(AggregateCall expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return switch (expression) {
            case AggregateCallImpl e ->
                String.format("%s(%s)", e.operator().name(), e.expression().accept(sqlRenderer, ctx));
            case CountDistinct e ->
                String.format("COUNT(DISTINCT %s)", e.expression().accept(sqlRenderer, ctx));
            case CountStar e -> "COUNT(*)";
            default -> throw new IllegalArgumentException("Unexpected value: " + expression);
        };
    }
}
