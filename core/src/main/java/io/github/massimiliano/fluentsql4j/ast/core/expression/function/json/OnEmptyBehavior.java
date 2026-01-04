package io.github.massimiliano.fluentsql4j.ast.core.expression.function.json;

/**
 * Represents the behavior when a JSON path expression returns empty.
 * <p>
 * This class combines the behavior kind (NULL, ERROR, DEFAULT) with an optional default value.
 * According to SQL:2016 standard, when using DEFAULT behavior, a default value must be provided.
 *
 * @param kind the behavior kind (NULL, ERROR, or DEFAULT)
 * @param defaultValue the default value to use when kind is DEFAULT (ignored for NULL and ERROR)
 * @since 1.0
 */
public record OnEmptyBehavior(BehaviorKind kind, String defaultValue) {

    /**
     * Compact constructor that validates the behavior.
     */
    public OnEmptyBehavior {
        if (kind == null) {
            kind = BehaviorKind.NONE;
        }
    }

    /**
     * Creates an OnEmptyBehavior that returns NULL when empty.
     *
     * @return OnEmptyBehavior with NULL kind
     */
    public static OnEmptyBehavior returnNull() {
        return new OnEmptyBehavior(BehaviorKind.NONE, null);
    }

    /**
     * Creates an OnEmptyBehavior that raises an error when empty.
     *
     * @return OnEmptyBehavior with ERROR kind
     */
    public static OnEmptyBehavior error() {
        return new OnEmptyBehavior(BehaviorKind.ERROR, null);
    }

    /**
     * Creates an OnEmptyBehavior that returns a default value when empty.
     *
     * @param defaultValue the default value to return
     * @return OnEmptyBehavior with DEFAULT kind
     */
    public static OnEmptyBehavior defaultValue(String defaultValue) {
        return new OnEmptyBehavior(BehaviorKind.DEFAULT, defaultValue);
    }
}
