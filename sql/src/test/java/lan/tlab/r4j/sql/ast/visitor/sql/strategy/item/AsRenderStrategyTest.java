package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
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
        Alias as = new Alias("c");
        String sql = strategy.render(as, renderer, new AstContext());
        assertThat(sql).isEqualTo("AS c");
    }

    @Test
    void empty() {
        Alias as = Alias.nullObject();
        String sql = strategy.render(as, renderer, new AstContext());
        assertThat(sql).isEqualTo("");
    }
}
