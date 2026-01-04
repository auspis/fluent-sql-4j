package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.Concat;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlConcatPsStrategyTest {

    private StandardSqlConcatPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlConcatPsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void handleConcatWithTwoLiterals() {
        // Given
        Concat concat = Concat.concat(Literal.of("Hello"), Literal.of("World"));

        // When
        PreparedStatementSpec result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT(?, ?)");
        assertThat(result.parameters()).containsExactly("Hello", "World");
    }

    @Test
    void handleConcatWithLiteralAndColumn() {
        // Given
        Concat concat = Concat.concat(Literal.of("Hello "), ColumnReference.of("users", "name"));

        // When
        PreparedStatementSpec result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT(?, \"name\")");
        assertThat(result.parameters()).containsExactly("Hello ");
    }

    @Test
    void handleConcatWithSeparator() {
        // Given
        Concat concat = Concat.concatWithSeparator(" - ", Literal.of("First"), Literal.of("Second"));

        // When
        PreparedStatementSpec result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT_WS(?, ?, ?)");
        assertThat(result.parameters()).containsExactly(" - ", "First", "Second");
    }

    @Test
    void handleConcatWithMultipleExpressions() {
        // Given
        Concat concat = Concat.concat(
                Literal.of("Start"),
                ColumnReference.of("table", "col1"),
                Literal.of("Middle"),
                ColumnReference.of("table", "col2"),
                Literal.of("End"));

        // When
        PreparedStatementSpec result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT(?, \"col1\", ?, \"col2\", ?)");
        assertThat(result.parameters()).containsExactly("Start", "Middle", "End");
    }

    @Test
    void handleConcatWithSeparatorAndMultipleExpressions() {
        // Given
        Concat concat = Concat.concatWithSeparator(", ", Literal.of("One"), Literal.of("Two"), Literal.of("Three"));

        // When
        PreparedStatementSpec result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT_WS(?, ?, ?, ?)");
        assertThat(result.parameters()).containsExactly(", ", "One", "Two", "Three");
    }
}
