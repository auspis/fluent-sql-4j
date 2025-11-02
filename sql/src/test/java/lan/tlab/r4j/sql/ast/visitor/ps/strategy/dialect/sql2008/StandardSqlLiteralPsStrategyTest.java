package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlLiteralPsStrategyTest {

    private StandardSqlLiteralPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlLiteralPsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void stringLiteral() {
        Literal<String> literal = Literal.of("Hello World");

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void integerLiteral() {
        Literal<Number> literal = Literal.of(42);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void longLiteral() {
        var literal = Literal.of(123456789L);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(123456789L);
    }

    @Test
    void doubleLiteral() {
        var literal = Literal.of(3.14159);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(3.14159);
    }

    @Test
    void floatLiteral() {
        var literal = Literal.of(2.71f);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(2.71f);
    }

    @Test
    void booleanTrueLiteral() {
        Literal<Boolean> literal = Literal.of(true);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(true);
    }

    @Test
    void booleanFalseLiteral() {
        Literal<Boolean> literal = Literal.of(false);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(false);
    }

    @Test
    void bigDecimalLiteral() {
        BigDecimal value = new BigDecimal("999.99");
        var literal = Literal.of(value);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(value);
    }

    @Test
    void localDateLiteral() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        Literal<LocalDate> literal = Literal.of(date);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(date);
    }

    @Test
    void localDateTimeLiteral() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 14, 30, 0);
        Literal<LocalDateTime> literal = Literal.of(dateTime);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(dateTime);
    }

    @Test
    void emptyStringLiteral() {
        Literal<String> literal = Literal.of("");

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("");
    }

    @Test
    void stringWithSpecialCharacters() {
        Literal<String> literal = Literal.of("O'Reilly & Associates");

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("O'Reilly & Associates");
    }

    @Test
    void zeroInteger() {
        var literal = Literal.of(0);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(0);
    }

    @Test
    void negativeInteger() {
        var literal = Literal.of(-42);

        PsDto result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(-42);
    }

    @Test
    void multipleCallsIndependent() {
        Literal<String> literal1 = Literal.of("first");
        Literal<String> literal2 = Literal.of("second");

        PsDto result1 = strategy.handle(literal1, visitor, ctx);
        PsDto result2 = strategy.handle(literal2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("?");
        assertThat(result1.parameters()).containsExactly("first");
        assertThat(result2.sql()).isEqualTo("?");
        assertThat(result2.parameters()).containsExactly("second");
    }
}
