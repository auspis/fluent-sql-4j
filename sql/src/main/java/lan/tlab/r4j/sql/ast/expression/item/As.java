package lan.tlab.r4j.sql.ast.expression.item;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Getter;

@Getter
public class As implements SqlItem {

    @Getter
    private final String name;

    public As(String name) {
        this.name = name;
    }

    public static As nullObject() {
        return new As("");
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
