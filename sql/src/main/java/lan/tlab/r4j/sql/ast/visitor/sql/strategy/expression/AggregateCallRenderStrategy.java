package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCallImpl;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.CountDistinct;
import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.CountStar;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class AggregateCallRenderStrategy implements ExpressionRenderStrategy {

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
