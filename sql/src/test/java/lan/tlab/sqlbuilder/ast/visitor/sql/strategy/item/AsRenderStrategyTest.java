package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AsRenderStrategyTest {

    private AsRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new AsRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        As as = new As("c");
        String sql = strategy.render(as, renderer, new AstContext());
        assertThat(sql).isEqualTo("AS c");
    }

    @Test
    void empty() {
        As as = As.nullObject();
        String sql = strategy.render(as, renderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }
}
