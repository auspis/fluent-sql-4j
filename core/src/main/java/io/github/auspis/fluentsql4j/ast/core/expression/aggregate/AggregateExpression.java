package io.github.auspis.fluentsql4j.ast.core.expression.aggregate;

import io.github.auspis.fluentsql4j.ast.core.expression.ValueExpression;

/**
 * Represents an aggregate expression that evaluates to a single summarized value for a group of
 * rows.
 *
 * <p>Aggregate expressions are fundamentally different from scalar expressions because:
 * <ul>
 *   <li>They produce a single value summarizing multiple rows in a group, not per-row values
 *   <li>They have strict usage restrictions:
 *     <ul>
 *       <li><b>SELECT</b>: Allowed - typically used to display aggregated results
 *       <li><b>WHERE</b>: NOT ALLOWED - aggregation happens after WHERE filtering
 *       <li><b>HAVING</b>: Allowed - filters groups after aggregation
 *       <li><b>GROUP BY</b>: NOT ALLOWED - you group BY scalar expressions, not aggregate results
 *       <li><b>ORDER BY</b>: Allowed - can order final results by aggregate values
 *     </ul>
 * </ul>
 *
 * <p>Examples of aggregate expressions:
 * <ul>
 *   <li>COUNT(*) - count all rows in a group
 *   <li>SUM(salary) - sum of all salaries in a group
 *   <li>AVG(price) - average price in a group
 *   <li>MAX(date) - maximum date in a group
 *   <li>COUNT(DISTINCT department) - count of distinct departments
 * </ul>
 *
 * <p>By extending {@link ValueExpression}, aggregate expressions can be used in the same contexts
 * as scalar expressions (comparisons, sorting), but with additional semantic restrictions enforced
 * at the clause level (e.g., WHERE vs HAVING).
 */
public interface AggregateExpression extends ValueExpression {}
