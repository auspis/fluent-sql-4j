package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.IntervalRenderStrategy;

public class StandardSqlIntervalRenderStrategy implements IntervalRenderStrategy {

    @Override
    public String render(Interval interval, SqlRenderer sqlRenderer, AstContext ctx) {
        return "INTERVAL " + interval.value().accept(sqlRenderer, ctx) + " "
                + interval.unit().name();
    }
}
