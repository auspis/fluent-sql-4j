package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlAsRenderStrategyTest {

    private StandardSqlAsRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlAsRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
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
