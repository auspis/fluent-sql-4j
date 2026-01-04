package io.github.massimiliano.fluentsql4j.ast.core.expression.function.number;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record Round(ScalarExpression numericExpression, ScalarExpression decimalPlaces) implements FunctionCall {

    public static Round of(Number value) {
        return new Round(Literal.of(value), new NullScalarExpression());
    }

    public static Round of(Number value, Number decimalPlaces) {
        return new Round(Literal.of(value), Literal.of(decimalPlaces));
    }

    public static Round of(ScalarExpression numericExpression) {
        return new Round(numericExpression, new NullScalarExpression());
    }

    public static Round of(ScalarExpression numericExpression, int number) {
        return new Round(numericExpression, Literal.of(number));
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
