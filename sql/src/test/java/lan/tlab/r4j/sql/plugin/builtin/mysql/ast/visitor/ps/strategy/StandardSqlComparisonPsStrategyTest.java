package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlComparisonPsStrategyTest {

    private StandardSqlComparisonPsStrategy strategy;
    private PreparedStatementRenderer renderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlComparisonPsStrategy();
        renderer = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void equalsComparison() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "name"), Literal.of("John"));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" = ?");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void notEqualsComparison() {
        Comparison comparison = Comparison.ne(ColumnReference.of("User", "status"), Literal.of("deleted"));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"status\" <> ?");
        assertThat(result.parameters()).containsExactly("deleted");
    }

    @Test
    void greaterThanComparison() {
        Comparison comparison = Comparison.gt(ColumnReference.of("User", "age"), Literal.of(18));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"age\" > ?");
        assertThat(result.parameters()).containsExactly(18);
    }

    @Test
    void greaterThanOrEqualsComparison() {
        Comparison comparison = Comparison.gte(ColumnReference.of("Product", "price"), Literal.of(100.0));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"price\" >= ?");
        assertThat(result.parameters()).containsExactly(100.0);
    }

    @Test
    void lessThanComparison() {
        Comparison comparison = Comparison.lt(ColumnReference.of("Order", "quantity"), Literal.of(50));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"quantity\" < ?");
        assertThat(result.parameters()).containsExactly(50);
    }

    @Test
    void lessThanOrEqualsComparison() {
        Comparison comparison = Comparison.lte(ColumnReference.of("User", "score"), Literal.of(1000));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"score\" <= ?");
        assertThat(result.parameters()).containsExactly(1000);
    }

    @Test
    void columnToColumnComparison() {
        Comparison comparison =
                Comparison.eq(ColumnReference.of("User", "created_by"), ColumnReference.of("User", "updated_by"));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"created_by\" = \"updated_by\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void differentTablesColumnComparison() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "id"), ColumnReference.of("Order", "user_id"));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"id\" = \"user_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void comparisonWithBooleanLiteral() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "is_active"), Literal.of(true));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"is_active\" = ?");
        assertThat(result.parameters()).containsExactly(true);
    }

    @Test
    void comparisonWithStringLiteral() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "email"), Literal.of("test@example.com"));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"email\" = ?");
        assertThat(result.parameters()).containsExactly("test@example.com");
    }

    @Test
    void comparisonWithIntegerLiteral() {
        Comparison comparison = Comparison.ne(ColumnReference.of("Product", "category_id"), Literal.of(42));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"category_id\" <> ?");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void comparisonWithDoubleLiteral() {
        Comparison comparison = Comparison.gt(ColumnReference.of("Product", "weight"), Literal.of(1.5));

        PsDto result = strategy.handle(comparison, renderer, ctx);

        assertThat(result.sql()).isEqualTo("\"weight\" > ?");
        assertThat(result.parameters()).containsExactly(1.5);
    }

    @Test
    void allComparisonOperators() {
        // Test all supported operators to ensure they map correctly
        assertThat(strategy.handle(Comparison.eq(ColumnReference.of("t", "c"), Literal.of("test1")), renderer, ctx)
                        .sql())
                .contains(" = ");
        assertThat(strategy.handle(Comparison.ne(ColumnReference.of("t", "c"), Literal.of("test2")), renderer, ctx)
                        .sql())
                .contains(" <> ");
        assertThat(strategy.handle(Comparison.gt(ColumnReference.of("t", "c"), Literal.of("test3")), renderer, ctx)
                        .sql())
                .contains(" > ");
        assertThat(strategy.handle(Comparison.gte(ColumnReference.of("t", "c"), Literal.of("test4")), renderer, ctx)
                        .sql())
                .contains(" >= ");
        assertThat(strategy.handle(Comparison.lt(ColumnReference.of("t", "c"), Literal.of("test5")), renderer, ctx)
                        .sql())
                .contains(" < ");
        assertThat(strategy.handle(Comparison.lte(ColumnReference.of("t", "c"), Literal.of("test6")), renderer, ctx)
                        .sql())
                .contains(" <= ");
    }
}
