package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.orderby.Sorting;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SortingRenderStrategyTest {

    private SortingRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new SortingRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
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
