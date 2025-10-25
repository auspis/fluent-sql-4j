package lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Getter;

@Getter
public class DateArithmetic implements FunctionCall {

    private final boolean add;
    private final ScalarExpression dateExpression;
    private final Interval interval;

    DateArithmetic(boolean add, ScalarExpression dateExpression, Interval interval) {
        this.add = add;
        this.dateExpression = dateExpression;
        this.interval = interval;
    }

    public static DateArithmetic add(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(true, dateExpression, interval);
    }

    public static DateArithmetic subtract(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(false, dateExpression, interval);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
