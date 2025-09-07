package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCallImpl;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.CountDistinct;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.CountStar;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class AggregateCallRenderStrategy implements ExpressionRenderStrategy {

    public String render(AggregateCall expression, SqlRenderer sqlRenderer) {
        return switch (expression) {
            case AggregateCallImpl e ->
                String.format(
                        "%s(%s)", e.getOperator().name(), e.getExpression().accept(sqlRenderer));
            case CountDistinct e ->
                String.format("COUNT(DISTINCT %s)", e.getExpression().accept(sqlRenderer));
            case CountStar e -> "COUNT(*)";
            default -> throw new IllegalArgumentException("Unexpected value: " + expression);
        };
    }
}
