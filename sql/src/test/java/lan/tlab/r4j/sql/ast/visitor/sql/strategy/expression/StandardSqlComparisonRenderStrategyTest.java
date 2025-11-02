package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.Test;

class StandardSqlComparisonRenderStrategyTest {

    private final StandardSqlComparisonRenderStrategy strategy = new StandardSqlComparisonRenderStrategy();
    private final SqlRenderer standardSql2008 = TestDialectRendererFactory.standardSql2008();

    @Test
    void eq() {
        Comparison expression = Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("mario"));
        String sql = strategy.render(expression, standardSql2008, new AstContext());

        assertThat(sql).isEqualTo("\"Customer\".\"name\" = 'mario'");
    }

    @Test
    void eq_columnReferences() {
        Comparison expression =
                Comparison.eq(ColumnReference.of("Customer", "name"), ColumnReference.of("tmp", "name"));
        String sql = strategy.render(expression, standardSql2008, new AstContext());

        assertThat(sql).isEqualTo("\"Customer\".\"name\" = \"tmp\".\"name\"");
    }

    @Test
    void ne() {
        Comparison expression = Comparison.ne(ColumnReference.of("Customer", "name"), Literal.of("mario"));
        String sql = strategy.render(expression, standardSql2008, new AstContext());

        assertThat(sql).isEqualTo("\"Customer\".\"name\" != 'mario'");
    }

    @Test
    void lt() {
        Comparison expression = Comparison.lt(ColumnReference.of("Customer", "score"), Literal.of(400));
        String sql = strategy.render(expression, standardSql2008, new AstContext());

        assertThat(sql).isEqualTo("\"Customer\".\"score\" < 400");
    }

    @Test
    void lt_columnReferences() {
        Comparison expression =
                Comparison.lt(ColumnReference.of("Customer", "score"), ColumnReference.of("tmp", "score"));
        String sql = strategy.render(expression, standardSql2008, new AstContext());

        assertThat(sql).isEqualTo("\"Customer\".\"score\" < \"tmp\".\"score\"");
    }

    @Test
    void lte() {
        Comparison expression = Comparison.lte(ColumnReference.of("Customer", "score"), Literal.of(400));
        String sql = strategy.render(expression, standardSql2008, new AstContext());

        assertThat(sql).isEqualTo("\"Customer\".\"score\" <= 400");
    }

    @Test
    void gt() {
        Comparison expression = Comparison.gt(ColumnReference.of("Customer", "score"), Literal.of(500));
        String sql = strategy.render(expression, standardSql2008, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"score\" > 500");
    }

    @Test
    void gte() {
        Comparison expression = Comparison.gte(ColumnReference.of("Customer", "score"), Literal.of(500));
        String sql = strategy.render(expression, standardSql2008, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"score\" >= 500");
    }
}
