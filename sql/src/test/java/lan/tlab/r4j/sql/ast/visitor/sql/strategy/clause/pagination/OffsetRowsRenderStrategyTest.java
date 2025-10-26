package lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.clause.fetch.OffsetRowsRenderStrategy;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OffsetRowsRenderStrategyTest {

    private OffsetRowsRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new OffsetRowsRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql2008();
    }

    @Test
    void empty() {
        Fetch pagination = new Fetch(0, null);

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }

    @Test
    void ok() {
        Fetch pagination = new Fetch(0, 10);

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY");

        pagination = new Fetch(16, 8);
        sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("OFFSET 16 ROWS FETCH NEXT 8 ROWS ONLY");
    }
}
