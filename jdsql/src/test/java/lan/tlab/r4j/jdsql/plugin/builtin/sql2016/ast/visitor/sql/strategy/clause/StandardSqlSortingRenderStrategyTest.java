package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.dql.clause.Sorting;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.clause.StandardSqlSortingRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlSortingRenderStrategyTest {

    private StandardSqlSortingRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlSortingRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void asc() {
        Sorting element = Sorting.asc(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(element, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"score\" ASC");
    }

    @Test
    void desc() {
        Sorting element = Sorting.desc(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(element, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"score\" DESC");
    }

    @Test
    void defaultOrder() {
        Sorting element = Sorting.by(ColumnReference.of("Customer", "score"));
        String sql = strategy.render(element, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\".\"score\"");
    }
}
