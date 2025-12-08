package lan.tlab.r4j.jdsql.ast.core.predicate;

import lan.tlab.r4j.jdsql.ast.core.expression.Expression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Between(Expression testExpression, Expression startExpression, Expression endExpression)
        implements Predicate {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
