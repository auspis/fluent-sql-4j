package lan.tlab.r4j.sql.functional;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Represents the result of an operation that can succeed or fail.
 * <p>
 * This sealed interface provides a type-safe way to handle success and failure cases
 * without using exceptions. It follows functional programming principles by making
 * errors explicit in the type system.
 *
 * @param <T> the type of the successful result
 */
public sealed interface Result<T> {

    record Success<T>(T value) implements Result<T> {
        public Success {
            if (value == null) {
                throw new IllegalArgumentException("Success value must not be null");
            }
        }
    }

    record Failure<T>(String message) implements Result<T> {
        public Failure {
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Failure message must not be null or blank");
            }
        }

        public IllegalArgumentException toException() {
            return new IllegalArgumentException(message);
        }
    }

    default <U> Result<U> map(Function<T, U> mapper) {
        return switch (this) {
            case Success<T>(T value) -> new Success<>(mapper.apply(value));
            case Failure<T> f -> new Failure<>(f.message());
        };
    }

    /**
     * Executes a side-effect action on the success value without transforming the result.
     * <p>
     * This method is useful for logging or other side effects while maintaining the functional
     * chain. The consumer is only called if this is a {@link Success}.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * DSL dsl = registry.dslFor("mysql", "8.0.35")
     *     .peek(d -> logger.info("MySQL DSL loaded successfully"))
     *     .orElseThrow();
     * }</pre>
     *
     * @param consumer the action to perform on the success value
     * @return this result unchanged
     */
    default Result<T> peek(Consumer<T> consumer) {
        if (this instanceof Success<T>(T value)) {
            consumer.accept(value);
        }
        return this;
    }

    default T orElseThrow() {
        return switch (this) {
            case Success<T>(T value) -> value;
            case Failure<T> f -> throw f.toException();
        };
    }

    default T orElse(T defaultValue) {
        return switch (this) {
            case Success<T>(T value) -> value;
            case Failure<T> ignored -> defaultValue;
        };
    }

    /**
     * Returns the success value if present, or computes a default value from the supplier.
     * <p>
     * Unlike {@link #orElse(Object)}, the supplier is only called if this is a {@link Failure},
     * making it suitable for expensive fallback operations.
     * <p>
     * <b>Example usage:</b>
     * <pre>{@code
     * DSL dsl = registry.dslFor("exotic-db", "1.0")
     *     .orElseGet(() -> registry.dslFor("standardsql", "2008").orElseThrow());
     * }</pre>
     *
     * @param supplier the supplier to compute the default value
     * @return the success value or the computed default
     */
    default T orElseGet(java.util.function.Supplier<T> supplier) {
        return switch (this) {
            case Success<T>(T value) -> value;
            case Failure<T> ignored -> supplier.get();
        };
    }

    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return this instanceof Failure<T>;
    }
}
