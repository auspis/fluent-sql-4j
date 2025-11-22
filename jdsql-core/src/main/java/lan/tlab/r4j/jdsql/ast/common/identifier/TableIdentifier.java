package lan.tlab.r4j.jdsql.ast.common.identifier;

import lan.tlab.r4j.jdsql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.jdsql.ast.dql.source.FromSource;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record TableIdentifier(String name, Alias alias) implements TableExpression, FromSource {

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
        return alias.name().isEmpty() ? name : alias.name();
    }

    public Alias getAs() {
        return alias;
    }
}
