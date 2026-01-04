package io.github.massimiliano.fluentsql4j.dsl.util;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Utility class for creating Literal expressions from Java values.
 */
public final class LiteralUtil {

    private LiteralUtil() {
        // Utility class - prevent instantiation
    }

    /**
     * Creates a Literal expression from a Java value.
     *
     * @param value the value to convert to a Literal
     * @return a Literal expression
     * @throws IllegalArgumentException if the value type is not supported
     */
    public static ScalarExpression createLiteral(Object value) {
        return switch (value) {
            case String s -> Literal.of(s);
            case Number n -> Literal.of(n);
            case Boolean b -> Literal.of(b);
            case LocalDate d -> Literal.of(d);
            case LocalDateTime dt -> Literal.of(dt);
            default -> throw new IllegalArgumentException("Unsupported type: " + value.getClass());
        };
    }
}
