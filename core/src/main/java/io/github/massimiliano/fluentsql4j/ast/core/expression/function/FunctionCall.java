package io.github.massimiliano.fluentsql4j.ast.core.expression.function;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;

/**
 * Represents a standard SQL scalar function call (e.g., LENGTH, CONCAT, UPPER, LOWER, ROUND,
 * YEAR, etc.).
 *
 * <p>FunctionCall is a marker interface for all standard SQL scalar functions that produce
 * one value per row. This includes:
 * <ul>
 *   <li>String functions: LENGTH, CONCAT, SUBSTRING, UPPER, LOWER, TRIM, LEFT, RIGHT, REPLACE
 *   <li>Numeric functions: ABS, ROUND, CEIL, FLOOR, POWER, SQRT, MOD
 *   <li>Date/Time functions: YEAR, MONTH, DAY, DATE_ADD, DATE_SUB, EXTRACT, NOW, CURRENT_DATE,
 *       CURRENT_TIMESTAMP
 *   <li>JSON functions: JSON_VALUE, JSON_QUERY, JSON_EXISTS
 * </ul>
 *
 * <p><b>Semantics</b>: Standard scalar functions produce exactly one value for each row being
 * evaluated. They are:
 * <ul>
 *   <li>✅ Valid in SELECT clause
 *   <li>✅ Valid in WHERE clause
 *   <li>✅ Valid in GROUP BY clause
 *   <li>✅ Valid in HAVING clause (as operands in comparisons)
 *   <li>✅ Valid in ORDER BY clause
 * </ul>
 *
 * <p><b>Distinction from Related Interfaces</b>:
 * <ul>
 *   <li>{@link WindowFunction}: Functions that operate on a window of rows and require an OVER
 *       clause (e.g., ROW_NUMBER, RANK, LAG, LEAD) - subset of ScalarExpression
 *   <li>{@link CustomFunctionCall}: Dialect-specific scalar functions not in standard SQL
 *       (e.g., MySQL's GROUP_CONCAT, PostgreSQL's STRING_AGG) - also ScalarExpression
 *   <li>{@link io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateExpression}: Functions
 *       that produce one value per group (COUNT, SUM, AVG, MAX, MIN) - separate hierarchy with
 *       different usage restrictions
 * </ul>
 *
 * <p><b>Implementation Categories</b>:
 * <ul>
 *   <li>String functions: {@code io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.*}
 *   <li>Numeric functions: {@code io.github.massimiliano.fluentsql4j.ast.core.expression.function.number.*}
 *   <li>Date/Time functions:
 *       {@code io.github.massimiliano.fluentsql4j.ast.core.expression.function.datetime.*}
 *   <li>JSON functions: {@code io.github.massimiliano.fluentsql4j.ast.core.expression.function.json.*}
 * </ul>
 *
 * @see ScalarExpression
 * @see WindowFunction
 * @see CustomFunctionCall
 * @see io.github.massimiliano.fluentsql4j.ast.core.expression.aggregate.AggregateExpression
 */
public interface FunctionCall extends ScalarExpression {}
