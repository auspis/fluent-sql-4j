package lan.tlab.r4j.sql.ast.identifier;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Getter;

@Getter
public class Alias implements Visitable {

    @Getter
    private final String name;

    public Alias(String name) {
        this.name = name;
    }

    public static Alias nullObject() {
        return new Alias("");
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
