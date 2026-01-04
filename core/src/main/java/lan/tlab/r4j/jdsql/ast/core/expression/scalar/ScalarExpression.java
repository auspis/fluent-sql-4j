package lan.tlab.r4j.jdsql.ast.core.expression.scalar;

import lan.tlab.r4j.jdsql.ast.core.expression.ValueExpression;

/**
 * Represents a scalar expression that evaluates to a single value per row.
 *
 * <p>Scalar expressions produce exactly one value for each row being evaluated. This is
 * fundamentally different from aggregate expressions which produce one value per group.
 *
 * <p>Examples of scalar expressions:
 * <ul>
 *   <li>Column references: {@code salary}, {@code department}
 *   <li>Literals: {@code 100}, {@code 'John'}, {@code TRUE}
 *   <li>Arithmetic: {@code salary * 1.1}, {@code price - discount}
 *   <li>Function calls: {@code UPPER(name)}, {@code YEAR(birthDate)}
 *   <li>CASE expressions: {@code CASE WHEN age >= 18 THEN 'Adult' ELSE 'Minor' END}
 *   <li>Scalar subqueries: {@code (SELECT name FROM employees WHERE id = 1)}
 * </ul>
 *
 * <p>Valid SQL contexts for scalar expressions:
 * <ul>
 *   <li><b>SELECT</b>: ✅ Always allowed
 *   <li><b>WHERE</b>: ✅ Always allowed (operates per-row)
 *   <li><b>GROUP BY</b>: ✅ Always allowed (grouping key)
 *   <li><b>HAVING</b>: ✅ Allowed (can compare with aggregates)
 *   <li><b>ORDER BY</b>: ✅ Always allowed
 * </ul>
 *
 * <p>Note: To use aggregate functions like SUM, COUNT, AVG in SELECT or HAVING clauses,
 * use {@link lan.tlab.r4j.jdsql.ast.core.expression.aggregate.AggregateExpression} instead.
 */
public interface ScalarExpression extends ValueExpression {}
