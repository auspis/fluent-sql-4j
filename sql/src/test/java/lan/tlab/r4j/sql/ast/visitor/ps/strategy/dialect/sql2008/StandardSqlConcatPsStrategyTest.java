package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlConcatPsStrategyTest {

    private StandardSqlConcatPsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlConcatPsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void handleConcatWithTwoLiterals() {
        // Given
        Concat concat = Concat.concat(Literal.of("Hello"), Literal.of("World"));

        // When
        PsDto result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT(?, ?)");
        assertThat(result.parameters()).containsExactly("Hello", "World");
    }

    @Test
    void handleConcatWithLiteralAndColumn() {
        // Given
        Concat concat = Concat.concat(Literal.of("Hello "), ColumnReference.of("users", "name"));

        // When
        PsDto result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT(?, \"name\")");
        assertThat(result.parameters()).containsExactly("Hello ");
    }

    @Test
    void handleConcatWithSeparator() {
        // Given
        Concat concat = Concat.concatWithSeparator(" - ", Literal.of("First"), Literal.of("Second"));

        // When
        PsDto result = strategy.handle(concat, visitor, ctx);

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
        PsDto result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT(?, \"col1\", ?, \"col2\", ?)");
        assertThat(result.parameters()).containsExactly("Start", "Middle", "End");
    }

    @Test
    void handleConcatWithSeparatorAndMultipleExpressions() {
        // Given
        Concat concat = Concat.concatWithSeparator(", ", Literal.of("One"), Literal.of("Two"), Literal.of("Three"));

        // When
        PsDto result = strategy.handle(concat, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CONCAT_WS(?, ?, ?, ?)");
        assertThat(result.parameters()).containsExactly(", ", "One", "Two", "Three");
    }
}
