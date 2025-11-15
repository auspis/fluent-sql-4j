package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAndOrPsStrategyTest {

    private StandardSqlAndOrPsStrategy strategy;
    private PreparedStatementRenderer renderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlAndOrPsStrategy();
        renderer = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void simpleAnd() {
        AndOr andExpression = AndOr.and(
                Comparison.eq(ColumnReference.of("User", "name"), Literal.of("John")),
                Comparison.gt(ColumnReference.of("User", "age"), Literal.of(18)));

        PsDto result = strategy.handle(andExpression, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"name\" = ?) AND (\"age\" > ?)");
        assertThat(result.parameters()).containsExactly("John", 18);
    }

    @Test
    void simpleOr() {
        AndOr orExpression = AndOr.or(
                Comparison.eq(ColumnReference.of("User", "status"), Literal.of("active")),
                Comparison.eq(ColumnReference.of("User", "status"), Literal.of("pending")));

        PsDto result = strategy.handle(orExpression, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"status\" = ?) OR (\"status\" = ?)");
        assertThat(result.parameters()).containsExactly("active", "pending");
    }

    @Test
    void multipleAndOperands() {
        AndOr andExpression = AndOr.and(
                Comparison.gt(ColumnReference.of("User", "age"), Literal.of(18)),
                Comparison.lt(ColumnReference.of("User", "age"), Literal.of(65)),
                Comparison.eq(ColumnReference.of("User", "status"), Literal.of("active")));

        PsDto result = strategy.handle(andExpression, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"age\" > ?) AND (\"age\" < ?) AND (\"status\" = ?)");
        assertThat(result.parameters()).containsExactly(18, 65, "active");
    }

    @Test
    void multipleOrOperands() {
        AndOr orExpression = AndOr.or(
                Comparison.eq(ColumnReference.of("User", "role"), Literal.of("admin")),
                Comparison.eq(ColumnReference.of("User", "role"), Literal.of("moderator")),
                Comparison.eq(ColumnReference.of("User", "role"), Literal.of("editor")));

        PsDto result = strategy.handle(orExpression, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"role\" = ?) OR (\"role\" = ?) OR (\"role\" = ?)");
        assertThat(result.parameters()).containsExactly("admin", "moderator", "editor");
    }

    @Test
    void nestedAndOr() {
        AndOr innerOr = AndOr.or(
                Comparison.eq(ColumnReference.of("User", "role"), Literal.of("admin")),
                Comparison.eq(ColumnReference.of("User", "role"), Literal.of("moderator")));

        AndOr outerAnd = AndOr.and(Comparison.gt(ColumnReference.of("User", "age"), Literal.of(18)), innerOr);

        PsDto result = strategy.handle(outerAnd, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"age\" > ?) AND ((\"role\" = ?) OR (\"role\" = ?))");
        assertThat(result.parameters()).containsExactly(18, "admin", "moderator");
    }

    @Test
    void nestedOrAnd() {
        AndOr innerAnd = AndOr.and(
                Comparison.gt(ColumnReference.of("User", "age"), Literal.of(25)),
                Comparison.eq(ColumnReference.of("User", "verified"), Literal.of(true)));

        AndOr outerOr = AndOr.or(Comparison.eq(ColumnReference.of("User", "role"), Literal.of("admin")), innerAnd);

        PsDto result = strategy.handle(outerOr, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"role\" = ?) OR ((\"age\" > ?) AND (\"verified\" = ?))");
        assertThat(result.parameters()).containsExactly("admin", 25, true);
    }

    @Test
    void complexNestedExpression() {
        AndOr leftOr = AndOr.or(
                Comparison.eq(ColumnReference.of("User", "status"), Literal.of("active")),
                Comparison.eq(ColumnReference.of("User", "status"), Literal.of("pending")));

        AndOr rightAnd = AndOr.and(
                Comparison.gt(ColumnReference.of("User", "score"), Literal.of(100)),
                Comparison.lt(ColumnReference.of("User", "score"), Literal.of(1000)));

        AndOr complexExpression = AndOr.and(leftOr, rightAnd);

        PsDto result = strategy.handle(complexExpression, renderer, ctx);

        assertThat(result.sql())
                .isEqualTo("((\"status\" = ?) OR (\"status\" = ?)) AND ((\"score\" > ?) AND (\"score\" < ?))");
        assertThat(result.parameters()).containsExactly("active", "pending", 100, 1000);
    }

    @Test
    void andWithDifferentComparisonOperators() {
        AndOr andExpression = AndOr.and(
                Comparison.ne(ColumnReference.of("User", "status"), Literal.of("deleted")),
                Comparison.gte(ColumnReference.of("User", "created_at"), Literal.of("2023-01-01")),
                Comparison.lte(ColumnReference.of("User", "last_login"), Literal.of("2024-12-31")));

        PsDto result = strategy.handle(andExpression, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"status\" <> ?) AND (\"created_at\" >= ?) AND (\"last_login\" <= ?)");
        assertThat(result.parameters()).containsExactly("deleted", "2023-01-01", "2024-12-31");
    }

    @Test
    void orWithDifferentComparisonOperators() {
        AndOr orExpression = AndOr.or(
                Comparison.eq(ColumnReference.of("User", "priority"), Literal.of("high")),
                Comparison.gt(ColumnReference.of("User", "score"), Literal.of(500)),
                Comparison.lt(ColumnReference.of("User", "days_inactive"), Literal.of(7)));

        PsDto result = strategy.handle(orExpression, renderer, ctx);

        assertThat(result.sql()).isEqualTo("(\"priority\" = ?) OR (\"score\" > ?) OR (\"days_inactive\" < ?)");
        assertThat(result.parameters()).containsExactly("high", 500, 7);
    }
}
