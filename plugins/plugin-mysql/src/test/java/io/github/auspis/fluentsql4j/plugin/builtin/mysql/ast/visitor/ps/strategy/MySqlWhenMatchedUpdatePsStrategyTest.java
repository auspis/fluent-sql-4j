package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class MySqlWhenMatchedUpdatePsStrategyTest {

    @Test
    void renderWithColumnReferencesUsingValues() {
        WhenMatchedUpdate item = new WhenMatchedUpdate(List.of(
                new UpdateItem(ColumnReference.of("", "name"), ColumnReference.of("src", "name")),
                new UpdateItem(ColumnReference.of("", "status"), ColumnReference.of("src", "status"))));

        AstToPreparedStatementSpecVisitor visitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlWhenMatchedUpdatePsStrategy strategy = new MySqlWhenMatchedUpdatePsStrategy();
        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());

        assertThat(result.sql())
                .isEqualTo("ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `status` = VALUES(`status`)");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void renderWithLiterals() {
        WhenMatchedUpdate item = new WhenMatchedUpdate(List.of(
                new UpdateItem(ColumnReference.of("", "status"), Literal.of("updated")),
                new UpdateItem(ColumnReference.of("", "active"), Literal.of(true))));

        AstToPreparedStatementSpecVisitor visitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlWhenMatchedUpdatePsStrategy strategy = new MySqlWhenMatchedUpdatePsStrategy();
        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("ON DUPLICATE KEY UPDATE `status` = ?, `active` = ?");
        assertThat(result.parameters()).containsExactly("updated", true);
    }

    @Test
    void renderWithMixedColumnReferencesAndLiterals() {
        WhenMatchedUpdate item = new WhenMatchedUpdate(List.of(
                new UpdateItem(ColumnReference.of("", "name"), ColumnReference.of("src", "name")),
                new UpdateItem(ColumnReference.of("", "modified_at"), Literal.of("2025-01-24"))));

        AstToPreparedStatementSpecVisitor visitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlWhenMatchedUpdatePsStrategy strategy = new MySqlWhenMatchedUpdatePsStrategy();
        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `modified_at` = ?");
        assertThat(result.parameters()).containsExactly("2025-01-24");
    }
}
