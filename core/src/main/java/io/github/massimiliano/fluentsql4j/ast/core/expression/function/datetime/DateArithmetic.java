package io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

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
