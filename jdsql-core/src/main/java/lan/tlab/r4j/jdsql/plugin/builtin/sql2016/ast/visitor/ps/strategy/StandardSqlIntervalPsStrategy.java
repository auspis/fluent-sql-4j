package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IntervalPsStrategy;

public class StandardSqlIntervalPsStrategy implements IntervalPsStrategy {

    @Override
    public PreparedStatementSpec handle(Interval interval, PreparedStatementRenderer renderer, AstContext ctx) {
        var valueResult = interval.value().accept(renderer, ctx);
        String unitName = interval.unit().name();

        String sql = String.format("INTERVAL %s %s", valueResult.sql(), unitName);
        return new PreparedStatementSpec(sql, valueResult.parameters());
    }
}
