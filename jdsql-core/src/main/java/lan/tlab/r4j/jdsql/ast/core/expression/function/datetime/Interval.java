package lan.tlab.r4j.jdsql.ast.core.expression.function.datetime;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Interval(ScalarExpression value, IntervalUnit unit) implements ScalarExpression {

    // TODO: generalize
    public enum IntervalUnit {
        YEAR,
        MONTH,
        DAY,
        HOUR,
        MINUTE,
        SECOND,
        WEEK, // Comune in MySQL, PostgreSQL con EXTRACT
        QUARTER,
        YEAR_MONTH, // MySQL specific (e.g., INTERVAL '1-2' YEAR_MONTH)
        DAY_HOUR, // MySQL specific (e.g., INTERVAL '1 2' DAY_HOUR)
        DAY_MINUTE, // MySQL specific
        DAY_SECOND, // MySQL specific
        HOUR_MINUTE, // MySQL specific
        HOUR_SECOND, // MySQL specific
        MINUTE_SECOND // MySQL specific
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
