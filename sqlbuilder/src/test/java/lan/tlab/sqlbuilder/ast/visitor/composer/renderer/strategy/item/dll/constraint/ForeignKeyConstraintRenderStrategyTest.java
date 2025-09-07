package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.ForeignKeyConstraint;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.ReferencesItem;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll.constraint.ForeignKeyConstraintRenderStrategy;

class ForeignKeyConstraintRenderStrategyTest {

    private ForeignKeyConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new ForeignKeyConstraintRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void singleColumn() {
        ForeignKeyConstraint fk = new ForeignKeyConstraint(List.of("customer_id"), new ReferencesItem("customer", "id"));
        String sql = strategy.render(fk, renderer);
        assertThat(sql).isEqualTo("FOREIGN KEY (\"customer_id\") REFERENCES \"customer\" (\"id\")");
    }

    @Test
    void composite() {
        ForeignKeyConstraint fk =
                new ForeignKeyConstraint(List.of("order_id", "product_id"), new ReferencesItem("order_product", "order_id", "product_id"));
        String sql = strategy.render(fk, renderer);
        assertThat(sql).isEqualTo("FOREIGN KEY (\"order_id\", \"product_id\") REFERENCES \"order_product\" (\"order_id\", \"product_id\")");
    }
}