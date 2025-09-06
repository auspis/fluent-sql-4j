package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ColumnDefinition;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import org.junit.jupiter.api.Test;

class TableDefinitionRenderStrategyTest {

    @Test
    void rendersTableWithColumnsAndPrimaryKey() {
        ColumnDefinition idCol = ColumnDefinition.integer("id");
        ColumnDefinition nameCol = ColumnDefinition.string("name");
        PrimaryKey pk = PrimaryKey.builder().column("id").build();
        TableDefinition tableDef = TableDefinition.builder()
                .table(new Table("my_table"))
                .primaryKey(pk)
                .columns(List.of(idCol, nameCol))
                .build();

        SqlRenderer renderer = SqlRendererImpl.builder().build();
        TableDefinitionRenderStrategy strategy = new TableDefinitionRenderStrategy();
        String sql = strategy.render(tableDef, renderer);

        assertThat(sql).isEqualTo("CREATE TABLE my_table (id INTEGER, name STRING, PRIMARY KEY (id))");
    }

    @Test
    void rendersTableWithColumnsWithoutPrimaryKey() {
        ColumnDefinition idCol = ColumnDefinition.integer("id");
        ColumnDefinition nameCol = ColumnDefinition.string("name");
        TableDefinition tableDef = TableDefinition.builder()
                .table(new Table("my_table"))
                .columns(List.of(idCol, nameCol))
                .primaryKey(null)
                .build();

        SqlRenderer renderer = SqlRendererImpl.builder().build();
        TableDefinitionRenderStrategy strategy = new TableDefinitionRenderStrategy();
        String sql = strategy.render(tableDef, renderer);

        assertThat(sql).isEqualTo("CREATE TABLE my_table (id INTEGER, name STRING)");
    }
}
