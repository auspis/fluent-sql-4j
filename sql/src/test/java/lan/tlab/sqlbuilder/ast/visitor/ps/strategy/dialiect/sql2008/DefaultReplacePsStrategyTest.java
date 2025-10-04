package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultReplacePsStrategyTest {

    @Test
    void handlesReplaceWithAllLiterals() {
        var strategy = new DefaultReplacePsStrategy();
        var replace = Replace.of(Literal.of("Hello World"), Literal.of("World"), Literal.of("Universe"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(?, ?, ?)");
        assertThat(result.parameters()).containsExactly("Hello World", "World", "Universe");
    }

    @Test
    void handlesReplaceWithColumnAndLiterals() {
        var strategy = new DefaultReplacePsStrategy();
        var replace = Replace.of(ColumnReference.of("users", "email"), Literal.of("@old.com"), Literal.of("@new.com"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(\"email\", ?, ?)");
        assertThat(result.parameters()).containsExactly("@old.com", "@new.com");
    }

    @Test
    void handlesReplaceWithAllColumns() {
        var strategy = new DefaultReplacePsStrategy();
        var replace = Replace.of(
                ColumnReference.of("content", "text"),
                ColumnReference.of("content", "old_pattern"),
                ColumnReference.of("content", "new_pattern"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(\"text\", \"old_pattern\", \"new_pattern\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesReplaceWithMixedExpressions() {
        var strategy = new DefaultReplacePsStrategy();
        var replace = Replace.of(
                Literal.of("Test string"), ColumnReference.of("patterns", "search"), Literal.of("replacement"));
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(replace, visitor, ctx);

        assertThat(result.sql()).isEqualTo("REPLACE(?, \"search\", ?)");
        assertThat(result.parameters()).containsExactly("Test string", "replacement");
    }
}
