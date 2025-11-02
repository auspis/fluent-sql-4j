package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateItemPsStrategy;
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
        PsDto result = strategy.handle(updateItem, visitor, ctx);

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
        PsDto result = strategy.handle(updateItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("\"email\" = ?");
        assertThat(result.parameters()).containsExactly("john@example.com");
    }
}
