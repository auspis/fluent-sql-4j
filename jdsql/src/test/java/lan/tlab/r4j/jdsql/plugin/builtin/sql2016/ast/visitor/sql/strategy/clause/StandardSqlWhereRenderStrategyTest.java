package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlWhereRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlWhereRenderStrategyTest {

    private StandardSqlWhereRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlWhereRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        Where where = Where.of(Comparison.eq(ColumnReference.of("Customer", "name"), Literal.of("Jack")));

        String sql = strategy.render(where, renderer, new AstContext());
        assertThat(sql).isEqualTo("WHERE \"Customer\".\"name\" = 'Jack'");
    }

    @Test
    void empty() {
        Where where = Where.nullObject();
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
