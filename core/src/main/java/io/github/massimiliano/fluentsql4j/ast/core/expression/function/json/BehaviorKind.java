package io.github.massimiliano.fluentsql4j.ast.core.expression.function.json;

/**
 * Represents the behavior options for JSON functions when handling empty or error conditions.
 * <p>
 * This enum defines the standard SQL:2016 behaviors that can be used with JSON_VALUE,
 * JSON_QUERY, and JSON_EXISTS functions.
 * <p>
 * According to SQL:2016 standard:
 * <ul>
 *   <li>{@link #NONE} - Returns NONE when the condition occurs</li>
 *   <li>{@link #ERROR} - Raises an error when the condition occurs</li>
 *   <li>{@link #DEFAULT} - Returns a default value when the condition occurs (requires a value)</li>
 * </ul>
 *
 * @since 1.0
 */
public enum BehaviorKind {
    /**
     * Returns NONE when the condition (empty or error) occurs.
     * This is typically the default behavior for most JSON functions.
     */
    NONE,

    /**
     * Raises an error when the condition (empty or error) occurs.
     * This behavior causes the query to fail if the condition is met.
     */
    ERROR,

    /**
     * Returns a default value when the condition (empty or error) occurs.
     * When using this behavior, a default value expression must be provided.
     */
    DEFAULT
}
