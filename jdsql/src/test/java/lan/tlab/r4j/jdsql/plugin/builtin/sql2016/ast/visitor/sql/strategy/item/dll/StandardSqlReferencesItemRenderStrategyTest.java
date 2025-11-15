package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlReferencesItemRenderStrategyTest {

    private StandardSqlReferencesItemRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlReferencesItemRenderStrategy();
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
