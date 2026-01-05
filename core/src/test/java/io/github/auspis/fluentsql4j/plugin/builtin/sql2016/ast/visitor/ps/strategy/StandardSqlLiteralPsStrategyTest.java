package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlLiteralPsStrategy;

class StandardSqlLiteralPsStrategyTest {

    private StandardSqlLiteralPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlLiteralPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void stringLiteral() {
        Literal<String> literal = Literal.of("Hello World");

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void integerLiteral() {
        Literal<Number> literal = Literal.of(42);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void longLiteral() {
        var literal = Literal.of(123456789L);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(123456789L);
    }

    @Test
    void doubleLiteral() {
        var literal = Literal.of(3.14159);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(3.14159);
    }

    @Test
    void floatLiteral() {
        var literal = Literal.of(2.71f);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(2.71f);
    }

    @Test
    void booleanTrueLiteral() {
        Literal<Boolean> literal = Literal.of(true);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(true);
    }

    @Test
    void booleanFalseLiteral() {
        Literal<Boolean> literal = Literal.of(false);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(false);
    }

    @Test
    void bigDecimalLiteral() {
        BigDecimal value = new BigDecimal("999.99");
        var literal = Literal.of(value);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(value);
    }

    @Test
    void localDateLiteral() {
        LocalDate date = LocalDate.of(2023, 12, 25);
        Literal<LocalDate> literal = Literal.of(date);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(date);
    }

    @Test
    void localDateTimeLiteral() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 12, 25, 14, 30, 0);
        Literal<LocalDateTime> literal = Literal.of(dateTime);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(dateTime);
    }

    @Test
    void emptyStringLiteral() {
        Literal<String> literal = Literal.of("");

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("");
    }

    @Test
    void stringWithSpecialCharacters() {
        Literal<String> literal = Literal.of("O'Reilly & Associates");

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly("O'Reilly & Associates");
    }

    @Test
    void zeroInteger() {
        var literal = Literal.of(0);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(0);
    }

    @Test
    void negativeInteger() {
        var literal = Literal.of(-42);

        PreparedStatementSpec result = strategy.handle(literal, visitor, ctx);

        assertThat(result.sql()).isEqualTo("?");
        assertThat(result.parameters()).containsExactly(-42);
    }

    @Test
    void multipleCallsIndependent() {
        Literal<String> literal1 = Literal.of("first");
        Literal<String> literal2 = Literal.of("second");

        PreparedStatementSpec result1 = strategy.handle(literal1, visitor, ctx);
        PreparedStatementSpec result2 = strategy.handle(literal2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("?");
        assertThat(result1.parameters()).containsExactly("first");
        assertThat(result2.sql()).isEqualTo("?");
        assertThat(result2.parameters()).containsExactly("second");
    }
}
