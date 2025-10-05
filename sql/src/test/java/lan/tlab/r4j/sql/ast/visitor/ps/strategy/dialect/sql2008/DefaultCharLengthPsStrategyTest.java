package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.CharLength;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultCharLengthPsStrategyTest {

    @Test
    void handlesCharLengthWithLiteralString() {
        var strategy = new DefaultCharLengthPsStrategy();
        var charLength = new CharLength(Literal.of("Hello World"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesCharLengthWithColumnReference() {
        var strategy = new DefaultCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("users", "email"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharLengthWithTableColumn() {
        var strategy = new DefaultCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("products", "description"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharLengthWithAliasedColumn() {
        var strategy = new DefaultCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("u", "username"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"username\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
