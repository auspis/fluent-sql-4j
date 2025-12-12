package lan.tlab.r4j.jdsql.ast.dql.projection;

import lan.tlab.r4j.jdsql.ast.core.expression.aggregate.AggregateExpression;
import lan.tlab.r4j.jdsql.ast.core.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Represents a projection with an {@link AggregateExpression}.
 *
 * <p>This is the type-safe counterpart to {@link ScalarExpressionProjection}, specifically for
 * aggregate expressions. It allows projecting aggregate function results (COUNT, SUM, AVG, etc.)
 * in a SELECT clause while maintaining type safety.
 *
 * <p>Example:
 * <pre>
 *   SELECT department, COUNT(*) AS count, AVG(salary) AS avg_salary
 *   // -> Projection with AggregateExpressionProjection for COUNT(*) and AVG(salary)
 * </pre>
 */
public class AggregateExpressionProjection extends Projection {

    public AggregateExpressionProjection(AggregateExpression expression) {
        super(expression);
    }

    public AggregateExpressionProjection(AggregateExpression expression, String as) {
        super(expression, as);
    }

    public AggregateExpressionProjection(AggregateExpression expression, Alias as) {
        super(expression, as);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
