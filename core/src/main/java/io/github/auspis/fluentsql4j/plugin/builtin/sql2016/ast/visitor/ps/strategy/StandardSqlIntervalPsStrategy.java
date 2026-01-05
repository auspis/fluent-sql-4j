package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.Interval;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IntervalPsStrategy;

public class StandardSqlIntervalPsStrategy implements IntervalPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            Interval interval, AstToPreparedStatementSpecVisitor astToPsSpecVisitor, AstContext ctx) {
        var valueResult = interval.value().accept(astToPsSpecVisitor, ctx);
        String unitName = interval.unit().name();

        String sql = String.format("INTERVAL %s %s", valueResult.sql(), unitName);
        return new PreparedStatementSpec(sql, valueResult.parameters());
    }
}
