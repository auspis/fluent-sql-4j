package lan.tlab.r4j.sql.plugin;

import java.util.function.Function;

/**
 * Represents the result of a registry operation.
 * <p>
 * This sealed interface provides a type-safe way to handle success and failure cases
 * without using exceptions. It follows functional programming principles by making
 * errors explicit in the type system.
 * <p>
 * <b>Example usage with pattern matching:</b>
 * <pre>{@code
 * RegistryResult<SqlRenderer> result = registry.getRenderer("mysql", "8.0.35");
 *
 * switch (result) {
 *     case Success<SqlRenderer>(SqlRenderer renderer) ->
 *         System.out.println("Got renderer: " + renderer);
 *     case Failure<SqlRenderer>(String message) ->
 *         System.err.println("Error: " + message);
 * }
 * }</pre>
 * <p>
 * <b>Example usage with helper methods:</b>
 * <pre>{@code
 * SqlRenderer renderer = registry.getRenderer("mysql", "8.0.35")
 *     .orElse(defaultRenderer);
 *
 * SqlRenderer renderer = registry.getRenderer("mysql", "8.0.35")
 *     .orElseThrow(); // Converts to exception if needed
 * }</pre>
 *
 * @param <T> the type of the successful result
 * @see SqlDialectPluginRegistry
 * @since 1.0
 */
public sealed interface RegistryResult<T> {

    /**
     * Represents a successful result containing a value.
     *
     * @param value the successful result value, never {@code null}
     * @param <T> the type of the value
     */
    record Success<T>(T value) implements RegistryResult<T> {
        public Success {
            if (value == null) {
                throw new IllegalArgumentException("Success value must not be null");
            }
        }
    }

    /**
     * Represents a failed result containing an error message.
     *
     * @param message the error message describing why the operation failed
     * @param <T> the type of the expected value
     */
    record Failure<T>(String message) implements RegistryResult<T> {
        public Failure {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Failure message must not be null or blank");
            }
        }

        /**
         * Converts this failure to an {@link IllegalArgumentException}.
         * <p>
         * This is useful when you need to integrate with code that expects exceptions.
         *
         * @return an exception containing the failure message
         */
        public IllegalArgumentException toException() {
            return new IllegalArgumentException(message);
        }
    }

    /**
     * Transforms the successful value using the given function.
     * <p>
     * If this is a {@link Success}, applies the mapper function to the value.
     * If this is a {@link Failure}, returns the failure unchanged.
     * <p>
     * This enables functional composition of registry operations.
     *
     * @param mapper the function to apply to a successful value
     * @param <U> the type of the mapped value
     * @return a new result with the mapped value, or the original failure
     */
    default <U> RegistryResult<U> map(Function<T, U> mapper) {
        return switch (this) {
            case Success<T>(T value) -> new Success<>(mapper.apply(value));
            case Failure<T> f -> new Failure<>(f.message());
        };
    }

    /**
     * Returns the successful value or throws an exception if this is a failure.
     * <p>
     * This method is useful when you want to integrate with code that expects exceptions,
     * or when you're confident the operation should succeed.
     *
     * @return the successful value
     * @throws IllegalArgumentException if this is a {@link Failure}
     */
    default T orElseThrow() {
        return switch (this) {
            case Success<T>(T value) -> value;
            case Failure<T> f -> throw f.toException();
        };
    }

    /**
     * Returns the successful value or the provided default if this is a failure.
     * <p>
     * This is useful for providing fallback values without handling failures explicitly.
     *
     * @param defaultValue the value to return if this is a failure
     * @return the successful value or the default value
     */
    default T orElse(T defaultValue) {
        return switch (this) {
            case Success<T>(T value) -> value;
            case Failure<T> ignored -> defaultValue;
        };
    }

    /**
     * Checks if this result represents a success.
     *
     * @return {@code true} if this is a {@link Success}, {@code false} otherwise
     */
    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    /**
     * Checks if this result represents a failure.
     *
     * @return {@code true} if this is a {@link Failure}, {@code false} otherwise
     */
    default boolean isFailure() {
        return this instanceof Failure<T>;
    }
}
