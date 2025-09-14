package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TableRenderStrategyTest {

    private TableRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new TableRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        Table table = new Table("Customer");
        String sql = strategy.render(table, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\"");
    }

    @Test
    void alias() {
        Table table = new Table("Customer", "c");
        String sql = strategy.render(table, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"Customer\" AS c");
    }
}
