package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.UnaryString.lower;
import static lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.string.UnaryString.upper;
import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UnaryStringPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlUnaryStringPsStrategyTest {

    private final UnaryStringPsStrategy strategy = new StandardSqlUnaryStringPsStrategy();
    private final PreparedStatementRenderer renderer =
            PreparedStatementRenderer.builder().build();
    private final AstContext ctx = new AstContext();

    @Test
    void lowerWithLiteral() {
        var lowerCall = lower(Literal.of("HELLO WORLD"));

        PsDto result = strategy.handle(lowerCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("LOWER(?)");
        assertThat(result.parameters()).containsExactly("HELLO WORLD");
    }

    @Test
    void lowerWithColumn() {
        var lowerCall = lower(ColumnReference.of("users", "name"));

        PsDto result = strategy.handle(lowerCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("LOWER(\"name\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void upperWithLiteral() {
        var upperCall = upper(Literal.of("hello world"));

        PsDto result = strategy.handle(upperCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("UPPER(?)");
        assertThat(result.parameters()).containsExactly("hello world");
    }

    @Test
    void upperWithColumn() {
        var upperCall = upper(ColumnReference.of("products", "description"));

        PsDto result = strategy.handle(upperCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("UPPER(\"description\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void lowerWithEmptyString() {
        var lowerCall = lower(Literal.of(""));

        PsDto result = strategy.handle(lowerCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("LOWER(?)");
        assertThat(result.parameters()).containsExactly("");
    }

    @Test
    void upperWithSpecialCharacters() {
        var upperCall = upper(Literal.of("test@email.com"));

        PsDto result = strategy.handle(upperCall, renderer, ctx);

        assertThat(result.sql()).isEqualTo("UPPER(?)");
        assertThat(result.parameters()).containsExactly("test@email.com");
    }
}
