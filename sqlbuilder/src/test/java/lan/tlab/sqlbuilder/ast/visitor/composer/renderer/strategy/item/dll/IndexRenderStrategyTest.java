package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Index;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;

class IndexRenderStrategyTest {

    private SqlRenderer renderer;
    private IndexRenderStrategy strategy;

    @BeforeEach
    void setUp() {
        renderer = SqlRendererFactory.standardSql2008();
        strategy = new IndexRenderStrategy();
    }

    @Test
    void single() {
        Index index = new Index("idx_email", "email");
        String sql = strategy.render(index, renderer);
        assertThat(sql).isEqualTo("INDEX \"idx_email\" (\"email\")");
    }

    @Test
    void composite() {
        Index index = new Index("idx_name_age", "name", "age");
        String sql = strategy.render(index, renderer);
        assertThat(sql).isEqualTo("INDEX \"idx_name_age\" (\"name\", \"age\")");
    }

}
