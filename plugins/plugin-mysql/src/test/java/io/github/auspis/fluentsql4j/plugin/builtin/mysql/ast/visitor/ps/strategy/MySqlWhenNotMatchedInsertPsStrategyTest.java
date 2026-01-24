package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.Test;

class MySqlWhenNotMatchedInsertPsStrategyTest {

    @Test
    void renderWithColumnListAndValueExpressions() {
        WhenNotMatchedInsert item = new WhenNotMatchedInsert(
                List.of(ColumnReference.of("", "id"), ColumnReference.of("", "name")),
                InsertData.InsertValues.of(ColumnReference.of("src", "id"), Literal.of("John")));

        AstToPreparedStatementSpecVisitor visitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlWhenNotMatchedInsertPsStrategy strategy = new MySqlWhenNotMatchedInsertPsStrategy();
        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(`id`, `name`) SELECT `src`.`id`, ?");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void renderWithoutColumnList() {
        WhenNotMatchedInsert item = new WhenNotMatchedInsert(
                List.of(), InsertData.InsertValues.of(Literal.of(1), Literal.of("Alice"), Literal.of("active")));

        AstToPreparedStatementSpecVisitor visitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlWhenNotMatchedInsertPsStrategy strategy = new MySqlWhenNotMatchedInsertPsStrategy();
        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo(" SELECT ?, ?, ?");
        assertThat(result.parameters()).containsExactly(1, "Alice", "active");
    }

    @Test
    void renderWithMixedLiteralsAndColumnReferences() {
        WhenNotMatchedInsert item = new WhenNotMatchedInsert(
                List.of(ColumnReference.of("", "id"), ColumnReference.of("", "name"), ColumnReference.of("", "status")),
                InsertData.InsertValues.of(ColumnReference.of("src", "id"), Literal.of("Bob"), Literal.of("pending")));

        AstToPreparedStatementSpecVisitor visitor = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlWhenNotMatchedInsertPsStrategy strategy = new MySqlWhenNotMatchedInsertPsStrategy();
        PreparedStatementSpec result = strategy.handle(item, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("(`id`, `name`, `status`) SELECT `src`.`id`, ?, ?");
        assertThat(result.parameters()).containsExactly("Bob", "pending");
    }
}
