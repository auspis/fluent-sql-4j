package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUpdateItemRenderStrategyTest {

    private StandardSqlUpdateItemRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlUpdateItemRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void literal() {
        UpdateItem item = UpdateItem.of("status", Literal.of("active"));
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"status\" = 'active'");
    }

    @Test
    void functionCall() {
        UpdateItem item = UpdateItem.of("updatedAt", new CurrentDateTime());
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"updatedAt\" = CURRENT_TIMESTAMP()");
    }

    @Test
    void arithmeticExpression() {
        UpdateItem item = UpdateItem.of(
                "price", ArithmeticExpression.multiplication(ColumnReference.of("", "price"), Literal.of(1.1)));
        String sql = strategy.render(item, renderer, new AstContext());
        assertThat(sql).isEqualTo("\"price\" = (\"price\" * 1.1)");
    }
}
