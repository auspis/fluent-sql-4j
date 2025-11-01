package lan.tlab.r4j.sql.ast.expression.scalar.call.function.json;

/**
 * Represents the behavior when a JSON path expression encounters an error.
 * <p>
 * This class wraps the behavior kind for error conditions.
 * According to SQL:2016 standard, error behavior can be NULL or ERROR.
 *
 * @param kind the behavior kind (NULL or ERROR)
 * @since 1.0
 */
public record OnErrorBehavior(BehaviorKind kind) {

    /**
     * Compact constructor that validates the behavior.
     */
    public OnErrorBehavior {
        if (kind == null) {
            kind = BehaviorKind.NULL;
        }
    }

    /**
     * Creates an OnErrorBehavior that returns NULL when an error occurs.
     *
     * @return OnErrorBehavior with NULL kind
     */
    public static OnErrorBehavior returnNull() {
        return new OnErrorBehavior(BehaviorKind.NULL);
    }

    /**
     * Creates an OnErrorBehavior that raises an error when an error occurs.
     *
     * @return OnErrorBehavior with ERROR kind
     */
    public static OnErrorBehavior error() {
        return new OnErrorBehavior(BehaviorKind.ERROR);
    }
}
