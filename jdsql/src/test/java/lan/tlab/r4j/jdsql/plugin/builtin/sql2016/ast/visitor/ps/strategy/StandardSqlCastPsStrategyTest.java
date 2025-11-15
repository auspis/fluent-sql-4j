package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.Cast;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCastPsStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCastPsStrategyTest {

    private StandardSqlCastPsStrategy strategy;
    private PreparedStatementRenderer renderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCastPsStrategy();
        renderer = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void handleCastWithLiteral() {
        // Given
        Cast cast = Cast.of(Literal.of("hello"), "VARCHAR(100)");

        // When
        PsDto result = strategy.handle(cast, renderer, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CAST(? AS VARCHAR(100))");
        assertThat(result.parameters()).containsExactly("hello");
    }

    @Test
    void handleCastWithColumnReference() {
        // Given
        Cast cast = Cast.of(ColumnReference.of("users", "user_id"), "VARCHAR(50)");

        // When
        PsDto result = strategy.handle(cast, renderer, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CAST(\"user_id\" AS VARCHAR(50))");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleCastWithDifferentDataTypes() {
        // Given INT cast
        Cast intCast = Cast.of(Literal.of("123"), "INT");

        // When
        PsDto intResult = strategy.handle(intCast, renderer, ctx);

        // Then
        assertThat(intResult.sql()).isEqualTo("CAST(? AS INT)");
        assertThat(intResult.parameters()).containsExactly("123");

        // Given DATE cast
        Cast dateCast = Cast.of(Literal.of("2023-01-01"), "DATE");

        // When
        PsDto dateResult = strategy.handle(dateCast, renderer, ctx);

        // Then
        assertThat(dateResult.sql()).isEqualTo("CAST(? AS DATE)");
        assertThat(dateResult.parameters()).containsExactly("2023-01-01");
    }
}
