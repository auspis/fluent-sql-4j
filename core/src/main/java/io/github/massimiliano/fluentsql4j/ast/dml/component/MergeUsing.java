package io.github.massimiliano.fluentsql4j.ast.dml.component;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record MergeUsing(TableExpression source) implements Visitable {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
