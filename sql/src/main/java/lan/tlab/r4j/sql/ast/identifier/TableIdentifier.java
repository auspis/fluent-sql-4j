package lan.tlab.r4j.sql.ast.identifier;

import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TableIdentifier implements TableExpression, FromSource {

    private String name = "";
    private Alias alias = Alias.nullObject();

    public TableIdentifier(String name) {
        this(name, Alias.nullObject());
    }

    public TableIdentifier(String name, String aliasName) {
        this(name, new Alias(aliasName));
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public String getTableReference() {
        return alias.getName().isEmpty() ? name : alias.getName();
    }

    public Alias getAs() {
        return alias;
    }
}
