package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultUpdateItemPsStrategyTest {

    private final DefaultUpdateItemPsStrategy strategy = new DefaultUpdateItemPsStrategy();

    @Test
    void shouldHandleUpdateItem() {
        // given
        var updateItem = UpdateItem.builder()
                .column(ColumnReference.of("table", "name"))
                .value(Literal.of("John"))
                .build();
        var visitor = PreparedStatementVisitor.builder().build();
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
        var visitor = PreparedStatementVisitor.builder().build();
        var ctx = new AstContext();

        // when
        PsDto result = strategy.handle(updateItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("\"email\" = ?");
        assertThat(result.parameters()).containsExactly("john@example.com");
    }
}
