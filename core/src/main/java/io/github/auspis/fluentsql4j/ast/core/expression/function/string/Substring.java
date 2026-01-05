package io.github.auspis.fluentsql4j.ast.core.expression.function.string;

import io.github.auspis.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.NullScalarExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record Substring(ScalarExpression expression, ScalarExpression startPosition, ScalarExpression length)
        implements FunctionCall {

    public static Substring of(ScalarExpression expression, int startPosition) {
        return of(expression, Literal.of(startPosition));
    }

    public static Substring of(ScalarExpression expression, int startPosition, int length) {
        return of(expression, Literal.of(startPosition), Literal.of(length));
    }

    public static Substring of(ScalarExpression expression, ScalarExpression startPosition) {
        return of(expression, startPosition, new NullScalarExpression());
    }

    public static Substring of(ScalarExpression expression, ScalarExpression startPosition, ScalarExpression length) {
        return new Substring(expression, startPosition, length);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
