package lan.tlab.sqlbuilder.ast.visitor.sql.dialect.sqlserver.strategy.clause.pagination;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.clause.pagination.Pagination;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.dialect.mysql.strategy.clause.pagination.MySqlLimitOffsetRenderStrategy;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlLimitOffsetRenderStrategyTest {

    private MySqlLimitOffsetRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new MySqlLimitOffsetRenderStrategy();
        sqlRenderer = SqlRendererFactory.mysql();
    }

    @Test
    void empty() {
        Pagination pagination = Pagination.builder().build();

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }

    @Test
    void ok() {
        Pagination pagination = Pagination.builder().page(0).perPage(10).build();

        String sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LIMIT 10 OFFSET 0");

        pagination = Pagination.builder().page(3).perPage(8).build();
        sql = strategy.render(pagination, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("LIMIT 8 OFFSET 16");
    }
}
