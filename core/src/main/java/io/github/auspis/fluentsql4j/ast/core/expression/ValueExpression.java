package io.github.auspis.fluentsql4j.ast.core.expression;

/**
 * Marker interface for expressions that produce values (as opposed to boolean predicates or set
 * operations).
 *
 * <p>ValueExpression includes:
 * <ul>
 *   <li>{@link io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression} - produces a value
 *       per row
 *   <li>{@link io.github.auspis.fluentsql4j.ast.core.expression.aggregate.AggregateExpression} - produces
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
 * <p>Note: {@link io.github.auspis.fluentsql4j.ast.core.predicate.Predicate} and
 * {@link io.github.auspis.fluentsql4j.ast.core.expression.set.SetExpression} do NOT extend ValueExpression
 * because they have different semantics and usage constraints.
 */
public interface ValueExpression extends Expression {}
