package io.github.auspis.fluentsql4j.ast.core.expression.scalar;

import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Literal<R>(R value) implements ScalarExpression {

    public static Literal<String> of(String value) {
        return new Literal<>(value);
    }

    public static Literal<Number> of(Number value) {
        return new Literal<>(value);
    }

    public static Literal<Boolean> of(Boolean value) {
        return new Literal<>(value);
    }

    public static Literal<LocalDate> of(LocalDate value) {
        return new Literal<>(value);
    }

    public static Literal<LocalDateTime> of(LocalDateTime value) {
        return new Literal<>(value);
    }

    public static Literal<Object> ofNull() {
        return new Literal<>(null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
