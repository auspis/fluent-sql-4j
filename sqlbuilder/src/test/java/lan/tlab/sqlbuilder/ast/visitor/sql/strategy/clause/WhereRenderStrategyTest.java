package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.conditional.where.Where;
import lan.tlab.sqlbuilder.ast.expression.bool.Comparison;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WhereRenderStrategyTest {

    private WhereRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new WhereRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
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
