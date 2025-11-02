package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.IntervalRenderStrategy;

public class StandardSqlIntervalRenderStrategy implements IntervalRenderStrategy {

    @Override
    public String render(Interval interval, SqlRenderer sqlRenderer, AstContext ctx) {
        return "INTERVAL " + interval.value().accept(sqlRenderer, ctx) + " "
                + interval.unit().name();
    }
}
