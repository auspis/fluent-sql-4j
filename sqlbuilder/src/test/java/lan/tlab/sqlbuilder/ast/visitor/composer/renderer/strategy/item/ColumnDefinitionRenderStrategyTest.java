package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import static org.junit.Assert.assertThat;

import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColumnDefinitionRenderStrategyTest {

    private SqlRenderer renderer;
    private ColumnDefinitionRenderStrategy strategy;

    @BeforeEach
    void setUp() {
        renderer = new StandardSqlRenderer();
        strategy = new ColumnDefinitionRenderStrategy();
    }

    @Test
    void testColumnDefinitionWithoutConstraints() {
        // "id" INT
        ColumnDefinition column = ColumnDefinition.builder()
                .columnName("id")
                .dataType("INT")
                .isNotNull(false)
                .isPrimaryKey(false)
                .build();

        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"id\" INT");
    }

    @Test
    void testColumnDefinitionWithNotNull() {
        // "name" VARCHAR(255) NOT NULL
        ColumnDefinition column = ColumnDefinition.builder()
                .columnName("name")
                .dataType("VARCHAR(255)")
                .isNotNull(true)
                .isPrimaryKey(false)
                .build();

        String sql = strategy.render(column, renderer);
        assertThat(sql).isEqualTo("\"name\" VARCHAR(255) NOT NULL");
    }
}
