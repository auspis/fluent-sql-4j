package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.IntervalPsStrategy;

public class DefaultIntervalPsStrategy implements IntervalPsStrategy {

    @Override
    public PsDto handle(Interval interval, PreparedStatementVisitor visitor, AstContext ctx) {
        var valueResult = interval.getValue().accept(visitor, ctx);
        String unitName = interval.getUnit().name();

        String sql = String.format("INTERVAL %s %s", valueResult.sql(), unitName);
        return new PsDto(sql, valueResult.parameters());
    }
}
