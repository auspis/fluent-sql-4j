package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.UpdateItemPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlUpdateItemPsStrategyTest {

    private final UpdateItemPsStrategy strategy = new StandardSqlUpdateItemPsStrategy();

    @Test
    void shouldHandleUpdateItem() {
        // given
        var updateItem = new UpdateItem(ColumnReference.of("table", "name"), Literal.of("John"));
        var visitor = PreparedStatementRenderer.builder().build();
        var ctx = new AstContext();

        // when
        PreparedStatementSpec result = strategy.handle(updateItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("\"name\" = ?");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void shouldHandleUpdateItemWithoutTableAlias() {
        // given
        var updateItem = UpdateItem.of("email", Literal.of("john@example.com"));
        var visitor = PreparedStatementRenderer.builder().build();
        var ctx = new AstContext();

        // when
        PreparedStatementSpec result = strategy.handle(updateItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("\"email\" = ?");
        assertThat(result.parameters()).containsExactly("john@example.com");
    }
}
