package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.CharacterLength;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlCharacterLengthPsStrategyTest {

    @Test
    void handlesCharacterLengthWithLiteralString() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(Literal.of("Hello World"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesCharacterLengthWithColumnReference() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(ColumnReference.of("users", "email"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharacterLengthWithTableColumn() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(ColumnReference.of("products", "description"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharacterLengthWithAliasedColumn() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(ColumnReference.of("c", "content"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(\"content\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
