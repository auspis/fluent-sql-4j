package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Length;
import lan.tlab.r4j.sql.ast.dql.clause.OrderBy;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlOrderByRenderStrategyTest {

    private StandardSqlOrderByRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlOrderByRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        OrderBy clause = OrderBy.of(Sorting.asc(ColumnReference.of("Customer", "name")));
        String sql = strategy.render(clause, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ORDER BY \"Customer\".\"name\" ASC");
    }

    @Test
    void multi() {
        OrderBy clause = OrderBy.of(
                Sorting.asc(ColumnReference.of("Customer", "last_name")),
                Sorting.desc(ColumnReference.of("Customer", "first_name")),
                Sorting.by(new Length(ColumnReference.of("Customer", "address"))));
        String sql = strategy.render(clause, sqlRenderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "ORDER BY \"Customer\".\"last_name\" ASC, \"Customer\".\"first_name\" DESC, LENGTH(\"Customer\".\"address\")");
    }
}
