package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Cast;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCastPsStrategyTest {

    private StandardSqlCastPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCastPsStrategy();
        specFactory = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void handleCastWithLiteral() {
        // Given
        Cast cast = Cast.of(Literal.of("hello"), "VARCHAR(100)");

        // When
        PreparedStatementSpec result = strategy.handle(cast, specFactory, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CAST(? AS VARCHAR(100))");
        assertThat(result.parameters()).containsExactly("hello");
    }

    @Test
    void handleCastWithColumnReference() {
        // Given
        Cast cast = Cast.of(ColumnReference.of("users", "user_id"), "VARCHAR(50)");

        // When
        PreparedStatementSpec result = strategy.handle(cast, specFactory, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CAST(\"user_id\" AS VARCHAR(50))");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleCastWithDifferentDataTypes() {
        // Given INT cast
        Cast intCast = Cast.of(Literal.of("123"), "INT");

        // When
        PreparedStatementSpec intResult = strategy.handle(intCast, specFactory, ctx);

        // Then
        assertThat(intResult.sql()).isEqualTo("CAST(? AS INT)");
        assertThat(intResult.parameters()).containsExactly("123");

        // Given DATE cast
        Cast dateCast = Cast.of(Literal.of("2023-01-01"), "DATE");

        // When
        PreparedStatementSpec dateResult = strategy.handle(dateCast, specFactory, ctx);

        // Then
        assertThat(dateResult.sql()).isEqualTo("CAST(? AS DATE)");
        assertThat(dateResult.parameters()).containsExactly("2023-01-01");
    }
}
