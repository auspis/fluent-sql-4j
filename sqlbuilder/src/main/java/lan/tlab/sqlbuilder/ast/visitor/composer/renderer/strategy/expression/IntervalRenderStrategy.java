package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class IntervalRenderStrategy implements ExpressionRenderStrategy {

    public String render(Interval interval, SqlRenderer sqlRenderer) {
        return "INTERVAL " + interval.getValue().accept(sqlRenderer) + " "
                + interval.getUnit().name();
    }
}
