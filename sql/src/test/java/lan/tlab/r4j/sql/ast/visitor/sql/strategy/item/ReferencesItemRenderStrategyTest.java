package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.ReferencesItemRenderStrategy;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ReferencesItemRenderStrategyTest {

    private ReferencesItemRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new ReferencesItemRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
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
