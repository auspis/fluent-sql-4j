package lan.tlab.r4j.sql.ast.expression.scalar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

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

    public static Literal<?> ofNull() {
        return new Literal<>(null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
