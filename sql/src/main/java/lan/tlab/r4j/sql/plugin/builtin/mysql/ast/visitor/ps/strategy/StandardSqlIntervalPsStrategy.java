package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IntervalPsStrategy;

public class StandardSqlIntervalPsStrategy implements IntervalPsStrategy {

    @Override
    public PsDto handle(Interval interval, PreparedStatementRenderer renderer, AstContext ctx) {
        var valueResult = interval.value().accept(renderer, ctx);
        String unitName = interval.unit().name();

        String sql = String.format("INTERVAL %s %s", valueResult.sql(), unitName);
        return new PsDto(sql, valueResult.parameters());
    }
}
