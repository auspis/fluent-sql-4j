package lan.tlab.r4j.jdsql.ast.common.expression.scalar.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;

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
 *   <li>new RowNumber(overClause)
 *   <li>new Rank(overClause)
 *   <li>new Lag(expression, offset, defaultValue, overClause)
 * </ul>
 */
public interface WindowFunction extends ScalarExpression {

    /**
     * Creates a ROW_NUMBER window function.
     *
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return a ROW_NUMBER window function
     */
    static RowNumber rowNumber(OverClause overClause) {
        return new RowNumber(overClause);
    }

    /**
     * Creates a RANK window function.
     *
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return a RANK window function
     */
    static Rank rank(OverClause overClause) {
        return new Rank(overClause);
    }

    /**
     * Creates a DENSE_RANK window function.
     *
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return a DENSE_RANK window function
     */
    static DenseRank denseRank(OverClause overClause) {
        return new DenseRank(overClause);
    }

    /**
     * Creates an NTILE window function.
     *
     * @param buckets the number of buckets to divide the rows into
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return an NTILE window function
     */
    static Ntile ntile(int buckets, OverClause overClause) {
        return new Ntile(buckets, overClause);
    }

    /**
     * Creates a LAG window function.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows back from the current row
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return a LAG window function
     */
    static Lag lag(ScalarExpression expression, int offset, OverClause overClause) {
        return new Lag(expression, offset, null, overClause);
    }

    /**
     * Creates a LAG window function with a default value.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows back from the current row
     * @param defaultValue the default value to return when the offset goes beyond the window
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return a LAG window function
     */
    static Lag lag(ScalarExpression expression, int offset, ScalarExpression defaultValue, OverClause overClause) {
        return new Lag(expression, offset, defaultValue, overClause);
    }

    /**
     * Creates a LEAD window function.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows forward from the current row
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return a LEAD window function
     */
    static Lead lead(ScalarExpression expression, int offset, OverClause overClause) {
        return new Lead(expression, offset, null, overClause);
    }

    /**
     * Creates a LEAD window function with a default value.
     *
     * @param expression the expression to evaluate
     * @param offset the number of rows forward from the current row
     * @param defaultValue the default value to return when the offset goes beyond the window
     * @param overClause the OVER clause specifying partitioning and ordering
     * @return a LEAD window function
     */
    static Lead lead(ScalarExpression expression, int offset, ScalarExpression defaultValue, OverClause overClause) {
        return new Lead(expression, offset, defaultValue, overClause);
    }
}
