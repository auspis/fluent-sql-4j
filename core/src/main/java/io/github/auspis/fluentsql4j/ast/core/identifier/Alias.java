package io.github.auspis.fluentsql4j.ast.core.identifier;

import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitable;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record Alias(String name) implements Visitable {

    public static Alias nullObject() {
        return new Alias("");
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
