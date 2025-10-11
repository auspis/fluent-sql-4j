package lan.tlab.r4j.sql.dsl.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import org.junit.jupiter.api.Test;

class LiteralUtilTest {

    @Test
    void createLiteralFromString() {
        ScalarExpression result = LiteralUtil.createLiteral("test");
        assertThat(result).isInstanceOf(Literal.class);
    }

    @Test
    void createLiteralFromInteger() {
        ScalarExpression result = LiteralUtil.createLiteral(42);
        assertThat(result).isInstanceOf(Literal.class);
    }

    @Test
    void createLiteralFromDouble() {
        ScalarExpression result = LiteralUtil.createLiteral(3.14);
        assertThat(result).isInstanceOf(Literal.class);
    }

    @Test
    void createLiteralFromBoolean() {
        ScalarExpression result = LiteralUtil.createLiteral(true);
        assertThat(result).isInstanceOf(Literal.class);
    }

    @Test
    void createLiteralFromLocalDate() {
        ScalarExpression result = LiteralUtil.createLiteral(LocalDate.of(2023, 1, 1));
        assertThat(result).isInstanceOf(Literal.class);
    }

    @Test
    void createLiteralFromLocalDateTime() {
        ScalarExpression result = LiteralUtil.createLiteral(LocalDateTime.of(2023, 1, 1, 12, 0));
        assertThat(result).isInstanceOf(Literal.class);
    }

    @Test
    void createLiteralWithUnsupportedTypeThrowsException() {
        assertThatThrownBy(() -> LiteralUtil.createLiteral(new Object()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported type");
    }
}
