package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultCastPsStrategyTest {

    private DefaultCastPsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultCastPsStrategy();
        visitor = new PreparedStatementVisitor();
        ctx = new AstContext();
    }

    @Test
    void handleCastWithLiteral() {
        // Given
        Cast cast = Cast.of(Literal.of("hello"), "VARCHAR(100)");

        // When
        PsDto result = strategy.handle(cast, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CAST(? AS VARCHAR(100))");
        assertThat(result.parameters()).containsExactly("hello");
    }

    @Test
    void handleCastWithColumnReference() {
        // Given
        Cast cast = Cast.of(ColumnReference.of("users", "user_id"), "VARCHAR(50)");

        // When
        PsDto result = strategy.handle(cast, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CAST(\"user_id\" AS VARCHAR(50))");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleCastWithDifferentDataTypes() {
        // Given INT cast
        Cast intCast = Cast.of(Literal.of("123"), "INT");

        // When
        PsDto intResult = strategy.handle(intCast, visitor, ctx);

        // Then
        assertThat(intResult.sql()).isEqualTo("CAST(? AS INT)");
        assertThat(intResult.parameters()).containsExactly("123");

        // Given DATE cast
        Cast dateCast = Cast.of(Literal.of("2023-01-01"), "DATE");

        // When
        PsDto dateResult = strategy.handle(dateCast, visitor, ctx);

        // Then
        assertThat(dateResult.sql()).isEqualTo("CAST(? AS DATE)");
        assertThat(dateResult.parameters()).containsExactly("2023-01-01");
    }
}
