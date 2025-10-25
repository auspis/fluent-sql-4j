package lan.tlab.r4j.sql.ast.expression.set;

import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

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
        return alias.getName();
    }
}
