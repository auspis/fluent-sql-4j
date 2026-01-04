package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.function.string.Substring;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlSubstringPsStrategyTest {

    private StandardSqlSubstringPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlSubstringPsStrategy();
        visitor = AstToPreparedStatementSpecVisitor.builder().build();
        ctx = new AstContext();
    }

    @Test
    void substringWithLiteralsNoLength() {
        Substring substring = Substring.of(Literal.of("Hello World"), 7);

        PreparedStatementSpec result = strategy.handle(substring, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUBSTRING(?, ?)");
        assertThat(result.parameters()).containsExactly("Hello World", 7);
    }

    @Test
    void substringWithLiteralsAndLength() {
        Substring substring = Substring.of(Literal.of("Hello World"), 1, 5);

        PreparedStatementSpec result = strategy.handle(substring, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUBSTRING(?, ?, ?)");
        assertThat(result.parameters()).containsExactly("Hello World", 1, 5);
    }

    @Test
    void substringWithColumnNoLength() {
        Substring substring = Substring.of(ColumnReference.of("users", "name"), 2);

        PreparedStatementSpec result = strategy.handle(substring, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUBSTRING(\"name\", ?)");
        assertThat(result.parameters()).containsExactly(2);
    }

    @Test
    void substringWithColumnAndLength() {
        Substring substring = Substring.of(ColumnReference.of("users", "email"), Literal.of(1), Literal.of(10));

        PreparedStatementSpec result = strategy.handle(substring, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUBSTRING(\"email\", ?, ?)");
        assertThat(result.parameters()).containsExactly(1, 10);
    }

    @Test
    void substringWithMixedExpressions() {
        Substring substring = Substring.of(
                ColumnReference.of("posts", "content"),
                Literal.of(1),
                ColumnReference.of("settings", "excerpt_length"));

        PreparedStatementSpec result = strategy.handle(substring, visitor, ctx);

        assertThat(result.sql()).isEqualTo("SUBSTRING(\"content\", ?, \"excerpt_length\")");
        assertThat(result.parameters()).containsExactly(1);
    }
}
