package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.UniqueConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UniqueConstraintRenderStrategyTest {

    private UniqueConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new UniqueConstraintRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql2008();
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
