package lan.tlab.r4j.jdsql.ast.dql.projection;

import lan.tlab.r4j.jdsql.ast.core.expression.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.core.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Represents a projection with an {@link AggregateCall}.
 *
 * <p>This is a specialized version of {@link AggregateExpressionProjection} specifically for
 * {@link AggregateCall} expressions (COUNT, SUM, AVG, MAX, MIN, and their variants like
 * COUNT(DISTINCT ...)).
 *
 * <p>Example:
 * <pre>
 *   SELECT COUNT(*) AS cnt, SUM(amount) AS total
 *   // -> AggregateCallProjection for COUNT(*) and SUM(amount)
 * </pre>
 */
public class AggregateCallProjection extends AggregateExpressionProjection {

    public AggregateCallProjection(AggregateCall expression) {
        super(expression);
    }

    public AggregateCallProjection(AggregateCall expression, String as) {
        super(expression, as);
    }

    public AggregateCallProjection(AggregateCall expression, Alias as) {
        super(expression, as);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
