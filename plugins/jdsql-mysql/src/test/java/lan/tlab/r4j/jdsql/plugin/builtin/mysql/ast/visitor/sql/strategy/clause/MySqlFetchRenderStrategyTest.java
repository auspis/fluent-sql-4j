package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.sql.strategy.clause;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.dql.clause.Fetch;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlFetchRenderStrategyTest {

    private MySqlFetchRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new MySqlFetchRenderStrategy();
        sqlRenderer = MysqlSqlRendererFactory.create();
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
