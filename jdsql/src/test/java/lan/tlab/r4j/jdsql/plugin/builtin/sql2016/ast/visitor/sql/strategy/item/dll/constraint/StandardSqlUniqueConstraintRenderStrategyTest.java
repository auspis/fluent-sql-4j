package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUniqueConstraintRenderStrategyTest {

    private StandardSqlUniqueConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlUniqueConstraintRenderStrategy();
        renderer = StandardSqlRendererFactory.standardSql();
    }

    @Test
    void ok() {
        UniqueConstraintDefinition unique = new UniqueConstraintDefinition("email");

        String sql = strategy.render(unique, renderer, new AstContext());
        assertThat(sql).isEqualTo("UNIQUE (\"email\")");
    }

    @Test
    void composite() {
        UniqueConstraintDefinition unique = new UniqueConstraintDefinition("customer_id", "order_id");

        String sql = strategy.render(unique, renderer, new AstContext());
        assertThat(sql).isEqualTo("UNIQUE (\"customer_id\", \"order_id\")");
    }
}
