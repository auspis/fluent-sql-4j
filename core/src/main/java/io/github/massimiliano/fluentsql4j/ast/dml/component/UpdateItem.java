package io.github.massimiliano.fluentsql4j.ast.dml.component;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record UpdateItem(ColumnReference column, ScalarExpression value) implements Visitable {

    public static UpdateItem of(String column, ScalarExpression value) {
        return new UpdateItem(ColumnReference.of("", column), value);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
