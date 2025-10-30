package lan.tlab.r4j.sql.ast.expression.scalar.call.window;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;

/**
 * Represents a window function introduced in SQL:2008 (e.g., ROW_NUMBER, RANK, DENSE_RANK, NTILE,
 * LAG, LEAD). Window functions perform calculations across a set of rows related to the current
 * row using the OVER clause.
 *
 * <p>Window functions operate on a "window" of rows and return a value for each row based on the
 * window specification defined by the OVER clause.
 *
 * <p>Examples:
 *
 * <ul>
 *   <li>ROW_NUMBER() OVER (ORDER BY salary DESC)
 *   <li>RANK() OVER (PARTITION BY department ORDER BY salary DESC)
 *   <li>LAG(salary, 1) OVER (ORDER BY hire_date)
 * </ul>
 */
public interface WindowFunction extends ScalarExpression {

    /**
     * Creates a ROW_NUMBER window function.
     *
     * @return a ROW_NUMBER window function
     */
    static RowNumber rowNumber() {
        return new RowNumber();
    }

    /**
     * Creates a RANK window function.
     *
     * @return a RANK window function
     */
    static Rank rank() {
        return new Rank();
    }

    /**
     * Creates a DENSE_RANK window function.
     *
     * @return a DENSE_RANK window function
     */
    static DenseRank denseRank() {
        return new DenseRank();
    }

    /**
     * Creates an NTILE window function.
     *
     * @param buckets the number of buckets to divide the rows into
     * @return an NTILE window function
     */
    static Ntile ntile(int buckets) {
        return new Ntile(buckets);
    }

    /**
     * Creates a LAG window function.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows back from the current row
     * @return a LAG window function
     */
    static Lag lag(ScalarExpression expression, int offset) {
        return new Lag(expression, offset);
    }

    /**
     * Creates a LAG window function with a default value.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows back from the current row
     * @param defaultValue the default value to return when the offset goes beyond the window
     * @return a LAG window function
     */
    static Lag lag(ScalarExpression expression, int offset, ScalarExpression defaultValue) {
        return new Lag(expression, offset, defaultValue);
    }

    /**
     * Creates a LEAD window function.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows forward from the current row
     * @return a LEAD window function
     */
    static Lead lead(ScalarExpression expression, int offset) {
        return new Lead(expression, offset);
    }

    /**
     * Creates a LEAD window function with a default value.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows forward from the current row
     * @param defaultValue the default value to return when the offset goes beyond the window
     * @return a LEAD window function
     */
    static Lead lead(ScalarExpression expression, int offset, ScalarExpression defaultValue) {
        return new Lead(expression, offset, defaultValue);
    }
}
