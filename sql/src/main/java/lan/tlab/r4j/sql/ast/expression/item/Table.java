package lan.tlab.r4j.sql.ast.expression.item;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Table implements SqlItem, TableExpression, FromSource {

    private String name = "";
    private As as = As.nullObject();

    public Table(String name) {
        this(name, As.nullObject());
    }

    public Table(String name, String alias) {
        this(name, new As(alias));
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public String getTableReference() {
        return as.getName().isEmpty() ? name : as.getName();
    }
}
