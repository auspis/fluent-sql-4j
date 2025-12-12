package lan.tlab.r4j.jdsql.ast.core.predicate;

import lan.tlab.r4j.jdsql.ast.core.expression.ValueExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Represents a BETWEEN predicate: {@code expression BETWEEN startExpression AND endExpression}.
 *
 * <p>All three expressions must be {@link ValueExpression value-producing expressions}, as BETWEEN
 * requires comparable values.
 */
public record Between(ValueExpression testExpression, ValueExpression startExpression, ValueExpression endExpression)
        implements Predicate {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
