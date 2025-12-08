package lan.tlab.r4j.jdsql.ast.core.expression.function.datetime;

import lan.tlab.r4j.jdsql.ast.core.expression.function.FunctionCall;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

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
