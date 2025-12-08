package lan.tlab.r4j.jdsql.ast.core.identifier;

import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitable;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Alias(String name) implements Visitable {

    public static Alias nullObject() {
        return new Alias("");
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
