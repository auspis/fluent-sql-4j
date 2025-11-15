package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlPrimaryKeyRenderStrategyTest {

    private StandardSqlPrimaryKeyRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlPrimaryKeyRenderStrategy();
        renderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void singleColumn() {
        PrimaryKeyDefinition pk = new PrimaryKeyDefinition("id");

        String sql = strategy.render(pk, renderer, new AstContext());
        assertThat(sql).isEqualTo("PRIMARY KEY (\"id\")");
    }

    @Test
    void composite() {
        PrimaryKeyDefinition pk = new PrimaryKeyDefinition("author_id", "book_id");

        String sql = strategy.render(pk, renderer, new AstContext());
        assertThat(sql).isEqualTo("PRIMARY KEY (\"author_id\", \"book_id\")");
    }
}
