package io.github.auspis.fluentsql4j.ast.core.expression.function.number;

import io.github.auspis.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record Power(ScalarExpression base, ScalarExpression exponent) implements FunctionCall {
    public static Power of(Number base, Number exponent) {
        return of(Literal.of(base), Literal.of(exponent));
    }

    public static Power of(ScalarExpression base, Number exponent) {
        return of(base, Literal.of(exponent));
    }

    public static Power of(Number base, ScalarExpression exponent) {
        return of(Literal.of(base), exponent);
    }

    public static Power of(ScalarExpression base, ScalarExpression exponent) {
        return new Power(base, exponent);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
