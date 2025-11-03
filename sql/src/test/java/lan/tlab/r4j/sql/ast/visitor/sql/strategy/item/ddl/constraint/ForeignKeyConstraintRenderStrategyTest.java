package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.ddl.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ReferencesItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ForeignKeyConstraintRenderStrategyTest {

    private ForeignKeyConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new ForeignKeyConstraintRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void singleColumn() {
        ForeignKeyConstraintDefinition fk =
                new ForeignKeyConstraintDefinition(List.of("customer_id"), new ReferencesItem("customer", "id"));
        String sql = strategy.render(fk, renderer, new AstContext());
        assertThat(sql).isEqualTo("FOREIGN KEY (\"customer_id\") REFERENCES \"customer\" (\"id\")");
    }

    @Test
    void composite() {
        ForeignKeyConstraintDefinition fk = new ForeignKeyConstraintDefinition(
                List.of("order_id", "product_id"), new ReferencesItem("order_product", "order_id", "product_id"));
        String sql = strategy.render(fk, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        "FOREIGN KEY (\"order_id\", \"product_id\") REFERENCES \"order_product\" (\"order_id\", \"product_id\")");
    }
}
