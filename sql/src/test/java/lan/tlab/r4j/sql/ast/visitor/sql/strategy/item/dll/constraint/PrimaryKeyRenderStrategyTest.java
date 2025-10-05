package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PrimaryKeyRenderStrategyTest {

    private PrimaryKeyRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new PrimaryKeyRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
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
