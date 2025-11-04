package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlIndexDefinitionRenderStrategyTest {

    private SqlRenderer renderer;
    private StandardSqlIndexDefinitionRenderStrategy strategy;

    @BeforeEach
    void setUp() {
        renderer = TestDialectRendererFactory.standardSql();
        strategy = new StandardSqlIndexDefinitionRenderStrategy();
    }

    @Test
    void single() {
        IndexDefinition index = new IndexDefinition("idx_email", "email");
        String sql = strategy.render(index, renderer, new AstContext());
        assertThat(sql).isEqualTo("INDEX \"idx_email\" (\"email\")");
    }

    @Test
    void composite() {
        IndexDefinition index = new IndexDefinition("idx_name_age", "name", "age");
        String sql = strategy.render(index, renderer, new AstContext());
        assertThat(sql).isEqualTo("INDEX \"idx_name_age\" (\"name\", \"age\")");
    }
}
