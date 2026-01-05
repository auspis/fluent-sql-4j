package io.github.auspis.fluentsql4j.ast.core.predicate;

import io.github.auspis.fluentsql4j.ast.core.expression.ValueExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents an IS NOT NULL predicate: {@code expression IS NOT NULL}.
 *
 * <p>Accepts {@link ValueExpression value-producing expressions}, which includes:
 * <ul>
 *   <li>Scalar expressions: columns, literals, functions
 *   <li>Aggregate expressions: COUNT, SUM, AVG, etc. (in GROUP BY context)
 * </ul>
 *
 * <p>Example SQL:
 * <pre>
 *   WHERE email IS NOT NULL
 *   HAVING COUNT(*) IS NOT NULL
 * </pre>
 */
public record IsNotNull(ValueExpression expression) implements Predicate {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
