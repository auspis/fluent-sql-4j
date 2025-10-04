package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.item.ddl.Index;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        String sql = strategy.render(index, renderer, new AstContext());
        assertThat(sql).isEqualTo("INDEX \"idx_email\" (\"email\")");
    }

    @Test
    void composite() {
        Index index = new Index("idx_name_age", "name", "age");
        String sql = strategy.render(index, renderer, new AstContext());
        assertThat(sql).isEqualTo("INDEX \"idx_name_age\" (\"name\", \"age\")");
    }
}
