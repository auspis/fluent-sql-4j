package lan.tlab.r4j.sql.functional;

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

    default boolean isSuccess() {
        return this instanceof Success<T>;
    }

    default boolean isFailure() {
        return this instanceof Failure<T>;
    }
}
