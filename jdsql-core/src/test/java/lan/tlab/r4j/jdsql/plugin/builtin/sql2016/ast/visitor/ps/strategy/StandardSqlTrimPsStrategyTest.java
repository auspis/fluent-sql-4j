package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static lan.tlab.r4j.jdsql.ast.core.expression.function.string.Trim.trim;
import static lan.tlab.r4j.jdsql.ast.core.expression.function.string.Trim.trimBoth;
import static lan.tlab.r4j.jdsql.ast.core.expression.function.string.Trim.trimLeading;
import static lan.tlab.r4j.jdsql.ast.core.expression.function.string.Trim.trimTrailing;
import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.TrimPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlTrimPsStrategyTest {

    private final TrimPsStrategy strategy = new StandardSqlTrimPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory =
            AstToPreparedStatementSpecVisitor.builder().build();
    private final AstContext ctx = new AstContext();

    @Test
    void simpleTrimWithLiteral() {
        var trimCall = trim(Literal.of("  hello  "));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(?)");
        assertThat(result.parameters()).containsExactly("  hello  ");
    }

    @Test
    void trimWithColumn() {
        var trimCall = trim(ColumnReference.of("users", "name"));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(\"name\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void trimBothWithLiteral() {
        var trimCall = trimBoth(Literal.of("  test  "));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(BOTH ?)");
        assertThat(result.parameters()).containsExactly("  test  ");
    }

    @Test
    void trimLeadingWithColumn() {
        var trimCall = trimLeading(ColumnReference.of("users", "description"));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(LEADING \"description\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void trimTrailingWithLiteral() {
        var trimCall = trimTrailing(Literal.of("  data  "));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(TRAILING ?)");
        assertThat(result.parameters()).containsExactly("  data  ");
    }

    @Test
    void trimWithCharactersToRemoveAndLiteral() {
        var trimCall = trim(Literal.of("*"), Literal.of("*hello*"));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(? FROM ?)");
        assertThat(result.parameters()).containsExactly("*", "*hello*");
    }

    @Test
    void trimBothWithCharactersToRemoveAndColumn() {
        var trimCall = trimBoth(Literal.of(" "), ColumnReference.of("users", "title"));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(BOTH ? FROM \"title\")");
        assertThat(result.parameters()).containsExactly(" ");
    }

    @Test
    void trimLeadingWithCharactersToRemoveMixed() {
        var trimCall = trimLeading(ColumnReference.of("config", "prefix"), Literal.of("prefix_data"));

        PreparedStatementSpec result = strategy.handle(trimCall, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("TRIM(LEADING \"prefix\" FROM ?)");
        assertThat(result.parameters()).containsExactly("prefix_data");
    }
}
