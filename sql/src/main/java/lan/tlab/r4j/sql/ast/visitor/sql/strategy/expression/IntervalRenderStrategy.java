package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface IntervalRenderStrategy extends ExpressionRenderStrategy {

    String render(Interval interval, SqlRenderer sqlRenderer, AstContext ctx);
}
