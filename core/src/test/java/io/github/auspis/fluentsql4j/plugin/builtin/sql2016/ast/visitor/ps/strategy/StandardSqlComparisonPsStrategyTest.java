package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlComparisonPsStrategyTest {

    private StandardSqlComparisonPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlComparisonPsStrategy();
        specFactory = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void equalsComparison() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "name"), Literal.of("John"));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"name\" = ?");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void notEqualsComparison() {
        Comparison comparison = Comparison.ne(ColumnReference.of("User", "status"), Literal.of("deleted"));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"status\" <> ?");
        assertThat(result.parameters()).containsExactly("deleted");
    }

    @Test
    void greaterThanComparison() {
        Comparison comparison = Comparison.gt(ColumnReference.of("User", "age"), Literal.of(18));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"age\" > ?");
        assertThat(result.parameters()).containsExactly(18);
    }

    @Test
    void greaterThanOrEqualsComparison() {
        Comparison comparison = Comparison.gte(ColumnReference.of("Product", "price"), Literal.of(100.0));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"price\" >= ?");
        assertThat(result.parameters()).containsExactly(100.0);
    }

    @Test
    void lessThanComparison() {
        Comparison comparison = Comparison.lt(ColumnReference.of("Order", "quantity"), Literal.of(50));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"quantity\" < ?");
        assertThat(result.parameters()).containsExactly(50);
    }

    @Test
    void lessThanOrEqualsComparison() {
        Comparison comparison = Comparison.lte(ColumnReference.of("User", "score"), Literal.of(1000));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"score\" <= ?");
        assertThat(result.parameters()).containsExactly(1000);
    }

    @Test
    void columnToColumnComparison() {
        Comparison comparison =
                Comparison.eq(ColumnReference.of("User", "created_by"), ColumnReference.of("User", "updated_by"));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"created_by\" = \"updated_by\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void differentTablesColumnComparison() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "id"), ColumnReference.of("Order", "user_id"));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"id\" = \"user_id\"");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void comparisonWithBooleanLiteral() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "is_active"), Literal.of(true));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"is_active\" = ?");
        assertThat(result.parameters()).containsExactly(true);
    }

    @Test
    void comparisonWithStringLiteral() {
        Comparison comparison = Comparison.eq(ColumnReference.of("User", "email"), Literal.of("test@example.com"));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"email\" = ?");
        assertThat(result.parameters()).containsExactly("test@example.com");
    }

    @Test
    void comparisonWithIntegerLiteral() {
        Comparison comparison = Comparison.ne(ColumnReference.of("Product", "category_id"), Literal.of(42));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"category_id\" <> ?");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void comparisonWithDoubleLiteral() {
        Comparison comparison = Comparison.gt(ColumnReference.of("Product", "weight"), Literal.of(1.5));

        PreparedStatementSpec result = strategy.handle(comparison, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("\"weight\" > ?");
        assertThat(result.parameters()).containsExactly(1.5);
    }

    @Test
    void allComparisonOperators() {
        // Test all supported operators to ensure they map correctly
        assertThat(strategy.handle(Comparison.eq(ColumnReference.of("t", "c"), Literal.of("test1")), specFactory, ctx)
                        .sql())
                .contains(" = ");
        assertThat(strategy.handle(Comparison.ne(ColumnReference.of("t", "c"), Literal.of("test2")), specFactory, ctx)
                        .sql())
                .contains(" <> ");
        assertThat(strategy.handle(Comparison.gt(ColumnReference.of("t", "c"), Literal.of("test3")), specFactory, ctx)
                        .sql())
                .contains(" > ");
        assertThat(strategy.handle(Comparison.gte(ColumnReference.of("t", "c"), Literal.of("test4")), specFactory, ctx)
                        .sql())
                .contains(" >= ");
        assertThat(strategy.handle(Comparison.lt(ColumnReference.of("t", "c"), Literal.of("test5")), specFactory, ctx)
                        .sql())
                .contains(" < ");
        assertThat(strategy.handle(Comparison.lte(ColumnReference.of("t", "c"), Literal.of("test6")), specFactory, ctx)
                        .sql())
                .contains(" <= ");
    }
}
