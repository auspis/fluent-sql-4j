package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.IntervalPsStrategy;

public class DefaultIntervalPsStrategy implements IntervalPsStrategy {

    @Override
    public PsDto handle(Interval interval, PreparedStatementVisitor visitor, AstContext ctx) {
        var valueResult = interval.getValue().accept(visitor, ctx);
        String unitName = interval.getUnit().name();

        String sql = String.format("INTERVAL %s %s", valueResult.sql(), unitName);
        return new PsDto(sql, valueResult.parameters());
    }
}
