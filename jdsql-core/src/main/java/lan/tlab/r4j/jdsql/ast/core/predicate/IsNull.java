package lan.tlab.r4j.jdsql.ast.core.predicate;

import lan.tlab.r4j.jdsql.ast.core.expression.ValueExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

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
