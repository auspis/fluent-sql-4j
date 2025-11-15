package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.aggregate.AggregateCall;
import lan.tlab.r4j.jdsql.ast.common.predicate.Like;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlLikePsStrategyTest {

    private StandardSqlLikePsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlLikePsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void columnReferenceWithPattern() {
        Like like = new Like(ColumnReference.of("User", "name"), "John%");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("John%");
    }

    @Test
    void columnReferenceWithWildcardPattern() {
        Like like = new Like(ColumnReference.of("Product", "description"), "%widget%");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"description\" LIKE ?");
        assertThat(result.parameters()).containsExactly("%widget%");
    }

    @Test
    void columnReferenceWithSuffixPattern() {
        Like like = new Like(ColumnReference.of("User", "email"), "%@example.com");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"email\" LIKE ?");
        assertThat(result.parameters()).containsExactly("%@example.com");
    }

    @Test
    void columnReferenceWithExactPattern() {
        Like like = new Like(ColumnReference.of("Category", "code"), "TECH");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"code\" LIKE ?");
        assertThat(result.parameters()).containsExactly("TECH");
    }

    @Test
    void columnReferenceWithSingleCharWildcard() {
        Like like = new Like(ColumnReference.of("User", "name"), "Jo_n");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("Jo_n");
    }

    @Test
    void literalExpressionWithPattern() {
        Like like = new Like(Literal.of("test string"), "%string%");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("? LIKE ?");
        assertThat(result.parameters()).containsExactly("test string", "%string%");
    }

    @Test
    void aggregateFunctionWithPattern() {
        Like like = new Like(AggregateCall.max(ColumnReference.of("User", "name")), "A%");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("MAX(\"name\") LIKE ?");
        assertThat(result.parameters()).containsExactly("A%");
    }

    @Test
    void emptyPattern() {
        Like like = new Like(ColumnReference.of("User", "name"), "");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("");
    }

    @Test
    void patternWithSpecialCharacters() {
        Like like = new Like(ColumnReference.of("User", "name"), "John's%");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("John's%");
    }

    @Test
    void patternWithEscapeCharacters() {
        Like like = new Like(ColumnReference.of("File", "path"), "C:\\%");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"path\" LIKE ?");
        assertThat(result.parameters()).containsExactly("C:\\%");
    }

    @Test
    void multipleTableColumnReference() {
        Like like1 = new Like(ColumnReference.of("User", "first_name"), "A%");
        Like like2 = new Like(ColumnReference.of("User", "last_name"), "%son");

        PsDto result1 = strategy.handle(like1, visitor, ctx);
        PsDto result2 = strategy.handle(like2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo("\"first_name\" LIKE ?");
        assertThat(result1.parameters()).containsExactly("A%");
        assertThat(result2.sql()).isEqualTo("\"last_name\" LIKE ?");
        assertThat(result2.parameters()).containsExactly("%son");
    }

    @Test
    void caseInsensitivePattern() {
        Like like = new Like(ColumnReference.of("User", "name"), "%JOHN%");

        PsDto result = strategy.handle(like, visitor, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" LIKE ?");
        assertThat(result.parameters()).containsExactly("%JOHN%");
    }
}
