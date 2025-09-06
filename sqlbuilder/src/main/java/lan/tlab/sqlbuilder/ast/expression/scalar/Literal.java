package lan.tlab.sqlbuilder.ast.expression.scalar;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Literal<R> implements ScalarExpression {
    private final R value;

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
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
