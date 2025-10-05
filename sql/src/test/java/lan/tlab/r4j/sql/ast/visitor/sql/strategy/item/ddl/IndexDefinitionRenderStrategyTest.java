package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.IndexDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexDefinitionRenderStrategyTest {

    private SqlRenderer renderer;
    private IndexDefinitionRenderStrategy strategy;

    @BeforeEach
    void setUp() {
        renderer = SqlRendererFactory.standardSql2008();
        strategy = new IndexDefinitionRenderStrategy();
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
