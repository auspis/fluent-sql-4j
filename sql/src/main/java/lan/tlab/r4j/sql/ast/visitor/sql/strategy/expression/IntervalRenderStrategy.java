package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class IntervalRenderStrategy implements ExpressionRenderStrategy {

    public String render(Interval interval, SqlRenderer sqlRenderer, AstContext ctx) {
        return "INTERVAL " + interval.getValue().accept(sqlRenderer, ctx) + " "
                + interval.getUnit().name();
    }
}
