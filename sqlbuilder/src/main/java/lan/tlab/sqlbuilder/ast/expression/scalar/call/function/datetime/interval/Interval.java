package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.interval;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Interval implements ScalarExpression {

    private final ScalarExpression value;
    private final IntervalUnit unit;

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

    public static Interval of(ScalarExpression value, IntervalUnit unit) {
        return new Interval(value, unit);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
