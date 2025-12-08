package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;

public interface IntervalPsStrategy {
    PreparedStatementSpec handle(Interval interval, PreparedStatementRenderer renderer, AstContext ctx);
}
