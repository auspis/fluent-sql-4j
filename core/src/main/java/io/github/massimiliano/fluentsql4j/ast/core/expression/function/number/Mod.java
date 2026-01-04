package io.github.massimiliano.fluentsql4j.ast.core.expression.function.number;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record Mod(ScalarExpression dividend, ScalarExpression divisor) implements FunctionCall {

    public static Mod of(Number dividend, Number divisor) {
        return new Mod(Literal.of(dividend), Literal.of(divisor));
    }

    public static Mod of(ScalarExpression dividend, Number divisor) {
        return new Mod(dividend, Literal.of(divisor));
    }

    public static Mod of(Number dividend, ScalarExpression divisor) {
        return new Mod(Literal.of(dividend), divisor);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
