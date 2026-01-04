package io.github.massimiliano.fluentsql4j.ast.core.expression.set;

import io.github.massimiliano.fluentsql4j.ast.core.identifier.Alias;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

public record AliasedTableExpression(TableExpression expression, Alias alias) implements TableExpression {

    public static AliasedTableExpression of(TableExpression expression, String alias) {
        return new AliasedTableExpression(expression, new Alias(alias));
    }

    public static AliasedTableExpression of(TableExpression expression, Alias alias) {
        return new AliasedTableExpression(expression, alias);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public String getTableReference() {
        return alias.name();
    }
}
