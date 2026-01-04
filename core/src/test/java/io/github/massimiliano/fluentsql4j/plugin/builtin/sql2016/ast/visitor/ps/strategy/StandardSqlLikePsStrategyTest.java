package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.core.predicate.Like;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlLikePsStrategyTest {

    private StandardSqlLikePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlLikePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void columnReferenceWithPattern() {
        Like like = new Like(ColumnReference.of("User", "name"), "John%");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("John%");
    }

    @Test
    void columnReferenceWithWildcardPattern() {
        Like like = new Like(ColumnReference.of("Product", "description"), "%widget%");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"description\" LIKE ?");
        assertThat(result.parameters()).containsExactly("%widget%");
    }

    @Test
    void columnReferenceWithSuffixPattern() {
        Like like = new Like(ColumnReference.of("User", "email"), "%@example.com");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"email\" LIKE ?");
        assertThat(result.parameters()).containsExactly("%@example.com");
    }

    @Test
    void columnReferenceWithExactPattern() {
        Like like = new Like(ColumnReference.of("Category", "code"), "TECH");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"code\" LIKE ?");
        assertThat(result.parameters()).containsExactly("TECH");
    }

    @Test
    void columnReferenceWithSingleCharWildcard() {
        Like like = new Like(ColumnReference.of("User", "name"), "Jo_n");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("Jo_n");
    }

    @Test
    void literalExpressionWithPattern() {
        Like like = new Like(Literal.of("test string"), "%string%");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? LIKE ?");
        assertThat(result.parameters()).containsExactly("test string", "%string%");
    }

    @Test
    void aggregateFunctionWithPattern() {
        // Using a column reference (scalar) instead of aggregate, as LIKE requires a scalar value
        Like like = new Like(ColumnReference.of("User", "name"), "A%");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("A%");
    }

    @Test
    void emptyPattern() {
        Like like = new Like(ColumnReference.of("User", "name"), "");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("");
    }

    @Test
    void patternWithSpecialCharacters() {
        Like like = new Like(ColumnReference.of("User", "name"), "John's%");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("John's%");
    }

    @Test
    void patternWithEscapeCharacters() {
        Like like = new Like(ColumnReference.of("File", "path"), "C:\\%");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"path\" LIKE ?");
        assertThat(result.parameters()).containsExactly("C:\\%");
    }

    @Test
    void multipleTableColumnReference() {
        Like like1 = new Like(ColumnReference.of("User", "first_name"), "A%");
        Like like2 = new Like(ColumnReference.of("User", "last_name"), "%son");

        PreparedStatementSpec result1 = strategy.handle(like1, visitor, ctx);
        PreparedStatementSpec result2 = strategy.handle(like2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("\"first_name\" LIKE ?");
        assertThat(result1.parameters()).containsExactly("A%");
        assertThat(result2.sql()).isEqualTo("\"last_name\" LIKE ?");
        assertThat(result2.parameters()).containsExactly("%son");
    }

    @Test
    void caseInsensitivePattern() {
        Like like = new Like(ColumnReference.of("User", "name"), "%JOHN%");

        PreparedStatementSpec result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("%JOHN%");
    }
}
