package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.function.string.CharacterLength;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlCharacterLengthPsStrategyTest {

    @Test
    void handlesCharacterLengthWithLiteralString() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(Literal.of("Hello World"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesCharacterLengthWithColumnReference() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(ColumnReference.of("users", "email"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharacterLengthWithTableColumn() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(ColumnReference.of("products", "description"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharacterLengthWithAliasedColumn() {
        var strategy = new StandardSqlCharacterLengthPsStrategy();
        var characterLength = new CharacterLength(ColumnReference.of("c", "content"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(characterLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHARACTER_LENGTH(\"content\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
