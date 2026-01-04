package lan.tlab.r4j.jdsql.ast.core.expression.aggregate;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;

/**
 * Represents a specific aggregate function call (e.g., COUNT, SUM, AVG, MAX, MIN).
 *
 * <p>This interface extends {@link AggregateExpression} to enforce that aggregate calls are
 * treated separately from scalar expressions in the type system. This provides compile-time
 * type safety for aggregate usage constraints.
 *
 * <p>Examples:
 * <ul>
 *   <li>COUNT(*) - count all rows
 *   <li>SUM(salary) - sum of salary column
 *   <li>AVG(price * quantity) - average of an expression
 *   <li>COUNT(DISTINCT department) - count of unique departments
 * </ul>
 */
public interface AggregateCall extends AggregateExpression {

    static AggregateCall max(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.MAX, expression);
    }

    static AggregateCall min(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.MIN, expression);
    }

    static AggregateCall avg(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.AVG, expression);
    }

    static AggregateCall sum(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.SUM, expression);
    }

    static AggregateCall count(ScalarExpression expression) {
        return new AggregateCallImpl(AggregateOperator.COUNT, expression);
    }

    static AggregateCall countStar() {
        return new CountStar();
    }

    static AggregateCall countDistinct(ScalarExpression expression) {
        return new CountDistinct(expression);
    }
}
