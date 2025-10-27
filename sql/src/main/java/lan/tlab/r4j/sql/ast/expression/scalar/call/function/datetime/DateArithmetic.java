package lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record DateArithmetic(Operation operation, ScalarExpression dateExpression, Interval interval)
        implements FunctionCall {

    public enum Operation {
        ADDITION,
        SUBTRACTION
    }

    public static DateArithmetic addition(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(Operation.ADDITION, dateExpression, interval);
    }

    public static DateArithmetic subtraction(ScalarExpression dateExpression, Interval interval) {
        return new DateArithmetic(Operation.SUBTRACTION, dateExpression, interval);
    }

    public boolean isAddition() {
        return operation == Operation.ADDITION;
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
