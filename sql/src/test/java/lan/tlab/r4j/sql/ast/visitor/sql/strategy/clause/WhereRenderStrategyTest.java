package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereRenderStrategyTest {

    private WhereRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new WhereRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        Where where = Where.of(Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("Jack")));

        String sql = strategy.render(where, renderer, new AstContext());
        assertThat(sql).isEqualTo("WHERE \"Customer\".\"name\" = 'Jack'");
    }

    @Test
    void empty() {
        Where where = Where.builder().build();
        String sql = strategy.render(where, renderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }

    @Test
    void andOf() {
        Where where = Where.andOf(
                Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("Jack")),
                Comparison.eq(ColumnReference.of("Customer", "surname"), Literal.of("W")));

        String sql = strategy.render(where, renderer, new AstContext());
        assertThat(sql).isEqualTo("WHERE (\"Customer\".\"name\" = 'Jack') AND (\"Customer\".\"surname\" = 'W')");
    }
}
