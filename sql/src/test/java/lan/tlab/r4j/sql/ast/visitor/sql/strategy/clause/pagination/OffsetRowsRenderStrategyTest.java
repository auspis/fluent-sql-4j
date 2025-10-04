package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.fetch.OffsetRowsRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OffsetRowsRenderStrategyTest {

    private OffsetRowsRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new OffsetRowsRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void empty() {
        Fetch pagination = Fetch.builder().build();

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }

    @Test
    void ok() {
        Fetch pagination = Fetch.builder().offset(0).rows(10).build();

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");

        pagination = Fetch.builder().offset(16).rows(8).build();
        sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("OFFSET 16 ROWS FETCH NEXT 8 ROWS ONLY");
    }
}
