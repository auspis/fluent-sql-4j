package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.string.CharLength;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCharLengthPsStrategy;

class StandardSqlCharLengthPsStrategyTest {

    @Test
    void handlesCharLengthWithLiteralString() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(Literal.of("Hello World"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(?)");
        assertThat(result.parameters()).containsExactly("Hello World");
    }

    @Test
    void handlesCharLengthWithColumnReference() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("users", "email"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"email\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharLengthWithTableColumn() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("products", "description"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"description\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCharLengthWithAliasedColumn() {
        var strategy = new StandardSqlCharLengthPsStrategy();
        var charLength = new CharLength(ColumnReference.of("u", "username"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(charLength, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CHAR_LENGTH(\"username\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }
}
