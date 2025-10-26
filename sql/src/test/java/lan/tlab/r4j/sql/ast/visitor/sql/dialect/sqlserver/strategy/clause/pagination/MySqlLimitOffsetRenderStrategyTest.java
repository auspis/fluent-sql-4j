package lan.tlab.r4j.sql.ast.visitor.sql.dialect.sqlserver.strategy.clause.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.clause.fetch.Fetch;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.dialect.mysql.strategy.clause.pagination.MySqlLimitOffsetRenderStrategy;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlLimitOffsetRenderStrategyTest {

    private MySqlLimitOffsetRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new MySqlLimitOffsetRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.mysql();
    }

    @Test
    void empty() {
        Fetch pagination = Fetch.nullObject();

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }

    @Test
    void ok() {
        Fetch pagination = new Fetch(0, 10);

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LIMIT 10 OFFSET 0");

        pagination = new Fetch(16, 8);
        sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LIMIT 8 OFFSET 16");
    }
}
