package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData.DefaultValues;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlDefaultValuesRenderStrategyTest {

    private StandardSqlDefaultValuesRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlDefaultValuesRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void ok() {
        DefaultValues item = new DefaultValues();
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("DEFAULT VALUES");
    }
}
