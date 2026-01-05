package io.github.auspis.fluentsql4j.ast.dql.source;

import io.github.auspis.fluentsql4j.ast.core.expression.set.AliasedTableExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.auspis.fluentsql4j.ast.core.identifier.Alias;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record FromSubquery(AliasedTableExpression aliased) implements FromSource {

    public static FromSubquery of(TableExpression expression, String alias) {
        return new FromSubquery(AliasedTableExpression.of(expression, alias));
    }

    public static FromSubquery of(TableExpression expression, Alias alias) {
        return new FromSubquery(AliasedTableExpression.of(expression, alias));
    }

    // Delegate methods for convenient access
    public TableExpression getExpression() {
        return aliased.expression();
    }

    public Alias getAlias() {
        return aliased.alias();
    }

    // For backward compatibility with existing code
    public TableExpression getSubquery() {
        return aliased.expression();
    }

    public Alias getAs() {
        return aliased.alias();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
