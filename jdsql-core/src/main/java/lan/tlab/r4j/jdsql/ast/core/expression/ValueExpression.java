package lan.tlab.r4j.jdsql.ast.core.expression;

/**
 * Marker interface for expressions that produce values (as opposed to boolean predicates or set
 * operations).
 *
 * <p>ValueExpression includes:
 * <ul>
 *   <li>{@link lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression} - produces a value
 *       per row
 *   <li>{@link lan.tlab.r4j.jdsql.ast.core.expression.aggregate.AggregateExpression} - produces
 *       a value per group
 * </ul>
 *
 * <p>This interface is used in contexts where both scalar and aggregate expressions are valid,
 * such as:
 * <ul>
 *   <li>Comparison operands (for WHERE and HAVING clauses)
 *   <li>ORDER BY expressions
 *   <li>SELECT projections
 * </ul>
 *
 * <p>Note: {@link lan.tlab.r4j.jdsql.ast.core.predicate.Predicate} and
 * {@link lan.tlab.r4j.jdsql.ast.core.expression.set.SetExpression} do NOT extend ValueExpression
 * because they have different semantics and usage constraints.
 */
public interface ValueExpression extends Expression {}
