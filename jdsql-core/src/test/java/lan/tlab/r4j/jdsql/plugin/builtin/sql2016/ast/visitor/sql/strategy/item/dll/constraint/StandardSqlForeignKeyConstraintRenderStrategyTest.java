package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll.constraint;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.ForeignKeyConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ReferencesItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlForeignKeyConstraintRenderStrategyTest {

    private StandardSqlForeignKeyConstraintRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlForeignKeyConstraintRenderStrategy();
        renderer = StandardSqlRendererFactory.standardSql();
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
