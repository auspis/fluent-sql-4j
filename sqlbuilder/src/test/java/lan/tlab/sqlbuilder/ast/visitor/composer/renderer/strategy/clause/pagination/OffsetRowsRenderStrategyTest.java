package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.clause.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OffsetRowsRenderStrategyTest {

    private OffsetRowsRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new OffsetRowsRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void empty() {
        Pagination pagination = Pagination.builder().build();

        String sql = strategy.render(pagination, sqlRenderer);
        assertThat(sql).isEqualTo("");
    }

    @Test
    void ok() {
        Pagination pagination = Pagination.builder().page(0).perPage(10).build();

        String sql = strategy.render(pagination, sqlRenderer);
        assertThat(sql).isEqualTo("OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");

        pagination = Pagination.builder().page(3).perPage(8).build();
        sql = strategy.render(pagination, sqlRenderer);
        assertThat(sql).isEqualTo("OFFSET 16 ROWS FETCH NEXT 8 ROWS ONLY");
    }
}
