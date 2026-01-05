package io.github.auspis.fluentsql4j.ast.core.predicate;

import io.github.auspis.fluentsql4j.ast.core.expression.ValueExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents an IS NULL predicate: {@code expression IS NULL}.
 *
 * <p>Accepts {@link ValueExpression value-producing expressions}, which includes:
 * <ul>
 *   <li>Scalar expressions: columns, literals, functions
 *   <li>Aggregate expressions: COUNT, SUM, AVG, etc. (in GROUP BY context)
 * </ul>
 *
 * <p>Example SQL:
 * <pre>
 *   WHERE email IS NULL
 *   HAVING COUNT(*) IS NULL
 * </pre>
 */
public record IsNull(ValueExpression expression) implements Predicate {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
