package io.github.auspis.fluentsql4j.ast.core.expression.function.string;

import io.github.auspis.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record Replace(ScalarExpression expression, ScalarExpression oldSubstring, ScalarExpression newSubstring)
        implements FunctionCall {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
