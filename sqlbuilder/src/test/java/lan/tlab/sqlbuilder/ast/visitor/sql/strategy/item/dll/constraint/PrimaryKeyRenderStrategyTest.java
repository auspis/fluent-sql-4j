package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
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
        PrimaryKey pk = new PrimaryKey("id");

        String sql = strategy.render(pk, renderer, new AstContext());
        assertThat(sql).isEqualTo("PRIMARY KEY (\"id\")");
    }

    @Test
    void composite() {
        PrimaryKey pk = new PrimaryKey("author_id", "book_id");

        String sql = strategy.render(pk, renderer, new AstContext());
        assertThat(sql).isEqualTo("PRIMARY KEY (\"author_id\", \"book_id\")");
    }
}
