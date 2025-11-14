package lan.tlab.r4j.sql.ast.common.predicate;

import lan.tlab.r4j.sql.ast.common.expression.Expression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Between(Expression testExpression, Expression startExpression, Expression endExpression)
        implements Predicate {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
