package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.StandardSqlTableRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlTableRenderStrategyTest {

    private StandardSqlTableRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlTableRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        TableIdentifier table = new TableIdentifier("Customer");
        String sql = strategy.render(table, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\"");
    }

    @Test
    void alias() {
        TableIdentifier table = new TableIdentifier("Customer", "c");
        String sql = strategy.render(table, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\" AS c");
    }
}
