package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.UniqueConstraint;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UniqueConstraintRenderStrategyTest {

    private UniqueConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new UniqueConstraintRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void ok() {
        UniqueConstraint unique = new UniqueConstraint("email");

        String sql = strategy.render(unique, renderer);
        assertThat(sql).isEqualTo("UNIQUE (\"email\")");
    }

    @Test
    void composite() {
        UniqueConstraint unique = new UniqueConstraint("customer_id", "order_id");

        String sql = strategy.render(unique, renderer);
        assertThat(sql).isEqualTo("UNIQUE (\"customer_id\", \"order_id\")");
    }
}
