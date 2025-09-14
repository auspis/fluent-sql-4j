package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.ReferencesItemRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReferencesItemRenderStrategyTest {

    private ReferencesItemRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new ReferencesItemRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        ReferencesItem item = new ReferencesItem("order_product", "order_id");
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("REFERENCES \"order_product\" (\"order_id\")");
    }

    @Test
    void manyColumns() {
        ReferencesItem item = new ReferencesItem("order_product", "order_id", "product_id");
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("REFERENCES \"order_product\" (\"order_id\", \"product_id\")");
    }
}
