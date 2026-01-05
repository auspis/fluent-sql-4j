package io.github.auspis.fluentsql4j.ast.core.predicate;

import io.github.auspis.fluentsql4j.ast.core.expression.ValueExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

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
