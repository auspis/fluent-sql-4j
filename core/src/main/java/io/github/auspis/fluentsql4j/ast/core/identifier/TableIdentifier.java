package io.github.auspis.fluentsql4j.ast.core.identifier;

import io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.auspis.fluentsql4j.ast.dql.source.FromSource;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

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
