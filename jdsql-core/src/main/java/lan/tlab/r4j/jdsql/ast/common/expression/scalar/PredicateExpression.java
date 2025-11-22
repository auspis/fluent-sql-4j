package lan.tlab.r4j.jdsql.ast.common.expression.scalar;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Wraps a {@link Predicate} so it can be used as a {@link ScalarExpression}.
 * <p>
 * This is necessary for database-specific functions that accept predicates as arguments,
 * such as MySQL's {@code IF(condition, true_value, false_value)} where the condition
 * is a predicate (e.g., {@code age >= 18}).
 * <p>
 * The {@code PredicateExpression} delegates rendering to the wrapped predicate,
 * allowing it to be used seamlessly in contexts expecting scalar expressions.
 * <p>
 * <b>Example Usage:</b>
 * <pre>{@code
 * Predicate condition = Comparison.gte(ColumnReference.of("age"), Literal.of(18));
 * ScalarExpression wrappedCondition = new PredicateExpression(condition);
 *
 * CustomFunctionCall ifCall = new CustomFunctionCall(
 *     "IF",
 *     List.of(wrappedCondition, Literal.of("adult"), Literal.of("minor")),
 *     Map.of()
 * );
 * // Renders as: IF(age >= 18, 'adult', 'minor')
 * }</pre>
 *
 * @param predicate the predicate to wrap as a scalar expression
 * @see CustomFunctionCall
 * @see Predicate
 */
public record PredicateExpression(Predicate predicate) implements ScalarExpression {

    /**
     * Creates a new PredicateExpression wrapping the given predicate.
     *
     * @param predicate the predicate to wrap
     * @throws IllegalArgumentException if predicate is null
     */
    public PredicateExpression {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
    }

    /**
     * Accepts a visitor by delegating to the wrapped predicate.
     * <p>
     * This allows the predicate to be rendered naturally in its boolean context
     * while being used as a scalar expression argument.
     *
     * @param visitor the visitor to accept
     * @param ctx the AST context
     * @param <T> the return type of the visitor
     * @return the result of visiting the wrapped predicate
     */
    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        // Delegate to the predicate's accept method
        // This allows the predicate to be rendered in its natural form
        return predicate.accept(visitor, ctx);
    }
}
