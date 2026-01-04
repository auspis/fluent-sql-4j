package io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record AggregateCallImpl(AggregateOperator operator, ScalarExpression expression) implements AggregateCall {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
