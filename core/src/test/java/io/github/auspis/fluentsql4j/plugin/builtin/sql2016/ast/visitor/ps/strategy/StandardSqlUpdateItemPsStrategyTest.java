package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UpdateItemPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlUpdateItemPsStrategy;

class StandardSqlUpdateItemPsStrategyTest {

    private final UpdateItemPsStrategy strategy = new StandardSqlUpdateItemPsStrategy();

    @Test
    void shouldHandleUpdateItem() {
        // given
        var updateItem = new UpdateItem(ColumnReference.of("table", "name"), Literal.of("John"));
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();
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
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();
        var ctx = new AstContext();

        // when
        PreparedStatementSpec result = strategy.handle(updateItem, visitor, ctx);

        // then
        assertThat(result.sql()).isEqualTo("\"email\" = ?");
        assertThat(result.parameters()).containsExactly("john@example.com");
    }
}
