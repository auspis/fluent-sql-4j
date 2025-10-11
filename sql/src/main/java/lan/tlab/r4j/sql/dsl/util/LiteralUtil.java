package lan.tlab.r4j.sql.dsl.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;

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
