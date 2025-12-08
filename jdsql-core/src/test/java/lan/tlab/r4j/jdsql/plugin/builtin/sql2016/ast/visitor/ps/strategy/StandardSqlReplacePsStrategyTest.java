package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Replace;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlReplacePsStrategyTest {

    @Test
    void handlesReplaceWithAllLiterals() {
        var strategy = new StandardSqlReplacePsStrategy();
        var replace = new Replace(Literal.of("Hello World"), Literal.of("World"), Literal.of("Universe"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(?, ?, ?)");
        assertThat(result.parameters()).containsExactly("Hello World", "World", "Universe");
    }

    @Test
    void handlesReplaceWithColumnAndLiterals() {
        var strategy = new StandardSqlReplacePsStrategy();
        var replace = new Replace(ColumnReference.of("users", "email"), Literal.of("@old.com"), Literal.of("@new.com"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(\"email\", ?, ?)");
        assertThat(result.parameters()).containsExactly("@old.com", "@new.com");
    }

    @Test
    void handlesReplaceWithAllColumns() {
        var strategy = new StandardSqlReplacePsStrategy();
        var replace = new Replace(
                ColumnReference.of("content", "text"),
                ColumnReference.of("content", "old_pattern"),
                ColumnReference.of("content", "new_pattern"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(\"text\", \"old_pattern\", \"new_pattern\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesReplaceWithMixedExpressions() {
        var strategy = new StandardSqlReplacePsStrategy();
        var replace = new Replace(
                Literal.of("Test string"), ColumnReference.of("patterns", "search"), Literal.of("replacement"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(?, \"search\", ?)");
        assertThat(result.parameters()).containsExactly("Test string", "replacement");
    }
}
