package lan.tlab.sqlbuilder.ast.expression.item;

import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
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
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
