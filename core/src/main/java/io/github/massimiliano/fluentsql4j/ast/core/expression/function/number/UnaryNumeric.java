package io.github.massimiliano.fluentsql4j.ast.core.expression.function.number;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record UnaryNumeric(FunctionName functionName, ScalarExpression numericExpression) implements FunctionCall {

    public enum FunctionName {
        ABS,
        CEIL,
        FLOOR,
        SQRT
    }

    public static UnaryNumeric abs(Number value) {
        return abs(Literal.of(value));
    }

    public static UnaryNumeric abs(ScalarExpression numericExpression) {
        return new UnaryNumeric(FunctionName.ABS, numericExpression);
    }

    public static UnaryNumeric ceil(Number value) {
        return ceil(Literal.of(value));
    }

    public static UnaryNumeric ceil(ScalarExpression numericExpression) {
        return new UnaryNumeric(FunctionName.CEIL, numericExpression);
    }

    public static UnaryNumeric floor(Number value) {
        return floor(Literal.of(value));
    }

    public static UnaryNumeric floor(ScalarExpression numericExpression) {
        return new UnaryNumeric(FunctionName.FLOOR, numericExpression);
    }

    public static UnaryNumeric sqrt(Number value) {
        return sqrt(Literal.of(value));
    }

    public static UnaryNumeric sqrt(ScalarExpression numericExpression) {
        return new UnaryNumeric(FunctionName.SQRT, numericExpression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
