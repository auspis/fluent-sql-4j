package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.CharLength;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlCharLengthPsStrategyTest {

    @Test
    void handlesCharLengthWithLiteralString() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(Literal.of("Hello World"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesCharLengthWithColumnReference() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("users", "email"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharLengthWithTableColumn() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("products", "description"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharLengthWithAliasedColumn() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("u", "username"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"username\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
