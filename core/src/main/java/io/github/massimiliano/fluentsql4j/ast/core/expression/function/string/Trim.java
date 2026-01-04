package io.github.massimiliano.fluentsql4j.ast.core.expression.function.string;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record Trim(ScalarExpression stringExpression, TrimMode mode, ScalarExpression charactersToRemove)
        implements FunctionCall {

    public enum TrimMode {
        BOTH,
        LEADING,
        TRAILING
    }

    public static Trim trim(ScalarExpression stringExpression) {
        return new Trim(stringExpression, null, null);
    }

    public static Trim trimBoth(ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.BOTH, null);
    }

    public static Trim trimLeading(ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.LEADING, null);
    }

    public static Trim trimTrailing(ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.TRAILING, null);
    }

    public static Trim trim(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, null, charactersToRemove);
    }

    public static Trim trimBoth(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.BOTH, charactersToRemove);
    }

    public static Trim trimLeading(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.LEADING, charactersToRemove);
    }

    public static Trim trimTrailing(ScalarExpression charactersToRemove, ScalarExpression stringExpression) {
        return new Trim(stringExpression, TrimMode.TRAILING, charactersToRemove);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
