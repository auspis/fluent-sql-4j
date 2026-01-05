package io.github.auspis.fluentsql4j.ast.core.expression.function.string;

import io.github.auspis.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record UnaryString(FunctionName functionName, ScalarExpression expression) implements FunctionCall {

    public enum FunctionName {
        LOWER,
        UPPER
    }

    public static UnaryString lower(ScalarExpression expression) {
        return new UnaryString(FunctionName.LOWER, expression);
    }

    public static UnaryString upper(ScalarExpression expression) {
        return new UnaryString(FunctionName.UPPER, expression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
