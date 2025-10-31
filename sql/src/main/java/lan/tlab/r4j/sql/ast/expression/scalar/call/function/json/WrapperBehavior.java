package lan.tlab.r4j.sql.ast.expression.scalar.call.function.json;

/**
 * Represents the wrapper behavior options for JSON_QUERY function.
 * <p>
 * This enum defines how the result should be wrapped when using the JSON_QUERY function
 * according to SQL:2016 standard.
 * <p>
 * Wrapper behaviors:
 * <ul>
 *   <li>{@link #NONE} - No wrapping, return the JSON fragment as-is (default behavior)</li>
 *   <li>{@link #WITH_WRAPPER} - Wrap the result in an array if it's a scalar, or to guarantee an array result</li>
 *   <li>{@link #WITHOUT_WRAPPER} - Explicitly specify no array wrapping</li>
 *   <li>{@link #WITH_CONDITIONAL_WRAPPER} - Conditionally wrap based on whether result is scalar or array</li>
 * </ul>
 *
 * @since 1.0
 */
public enum WrapperBehavior {
    /**
     * No wrapping applied. Returns the JSON fragment as-is.
     * This is the default behavior when no wrapper clause is specified.
     */
    NONE(null),

    /**
     * Wraps the result in an array.
     * If the result is a scalar value, it will be wrapped in an array.
     * Guarantees that the result is always an array.
     */
    WITH_WRAPPER("WITH WRAPPER"),

    /**
     * Explicitly specifies no array wrapping.
     * Returns the JSON fragment without any wrapper.
     */
    WITHOUT_WRAPPER("WITHOUT WRAPPER"),

    /**
     * Conditionally wraps the result based on whether it's a scalar or array.
     * Wraps only if necessary to maintain consistency.
     */
    WITH_CONDITIONAL_WRAPPER("WITH CONDITIONAL WRAPPER");

    private final String sqlClause;

    WrapperBehavior(String sqlClause) {
        this.sqlClause = sqlClause;
    }

    /**
     * Returns the SQL clause representation of this wrapper behavior.
     *
     * @return the SQL clause string, or null if no clause should be emitted
     */
    public String toSql() {
        return sqlClause;
    }

    /**
     * Returns true if this wrapper behavior requires a SQL clause.
     *
     * @return true if a SQL clause should be emitted, false otherwise
     */
    public boolean hasClause() {
        return sqlClause != null;
    }
}
