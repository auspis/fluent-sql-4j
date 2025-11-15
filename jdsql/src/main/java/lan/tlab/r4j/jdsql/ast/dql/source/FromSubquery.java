package lan.tlab.r4j.jdsql.ast.dql.source;

import lan.tlab.r4j.jdsql.ast.common.expression.set.AliasedTableExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.jdsql.ast.common.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

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
