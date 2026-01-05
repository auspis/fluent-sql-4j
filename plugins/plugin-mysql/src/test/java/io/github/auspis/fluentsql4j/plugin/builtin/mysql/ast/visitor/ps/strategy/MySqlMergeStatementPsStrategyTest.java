package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import java.util.List;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.MySqlMergeStatementPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy.MysqlEscapeStrategy;

class MySqlMergeStatementPsStrategyTest {

    @Test
    void mergeWithMatchedAndNotMatched_generatesParametrizedMySqlSyntax() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users", "tgt"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("tgt", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(
                        new WhenMatchedUpdate(List.of(
                                new UpdateItem(ColumnReference.of("", "name"), ColumnReference.of("src", "name")),
                                new UpdateItem(ColumnReference.of("", "status"), Literal.of("updated")))),
                        new WhenNotMatchedInsert(
                                List.of(
                                        ColumnReference.of("", "id"),
                                        ColumnReference.of("", "name"),
                                        ColumnReference.of("", "status")),
                                InsertData.InsertValues.of(
                                        ColumnReference.of("src", "id"),
                                        ColumnReference.of("src", "name"),
                                        Literal.of("new")))))
                .build();

        // TODO: use TestDialectPreparedStatementSpecFactoryFactory
        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();
        PreparedStatementSpec result = strategy.handle(stmt, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("""
                    INSERT INTO `users` (`id`, `name`, `status`) \
                    SELECT `src`.`id`, `src`.`name`, ? \
                    FROM `users_updates` AS src \
                    ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `status` = ?""");
        assertThat(result.parameters()).containsExactly("new", "updated");
    }

    @Test
    void mergeWithOnlyNotMatched_generatesInsertOnly() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(new WhenNotMatchedInsert(
                        List.of(ColumnReference.of("", "id"), ColumnReference.of("", "name")),
                        InsertData.InsertValues.of(ColumnReference.of("src", "id"), Literal.of("John")))))
                .build();

        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();
        PreparedStatementSpec result = strategy.handle(stmt, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("""
                    INSERT INTO `users` (`id`, `name`) \
                    SELECT `src`.`id`, ? \
                    FROM `users_updates` AS src""");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void mergeWithLiterals_generatesParametrizedSql() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), Literal.of(1)))
                .actions(List.of(
                        new WhenMatchedUpdate(
                                List.of(new UpdateItem(ColumnReference.of("", "status"), Literal.of("active")))),
                        new WhenNotMatchedInsert(
                                List.of(ColumnReference.of("", "id"), ColumnReference.of("", "status")),
                                InsertData.InsertValues.of(Literal.of(2), Literal.of("pending")))))
                .build();

        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();
        PreparedStatementSpec result = strategy.handle(stmt, specFactory, new AstContext());

        assertThat(result.sql()).isEqualTo("""
                    INSERT INTO `users` (`id`, `status`) \
                    SELECT ?, ? \
                    FROM `users_updates` AS src \
                    ON DUPLICATE KEY UPDATE `status` = ?""");
        assertThat(result.parameters()).containsExactly(2, "pending", "active");
    }

    @Test
    void missingWhenNotMatchedThrowsException() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(new WhenMatchedUpdate(
                        List.of(new UpdateItem(ColumnReference.of("", "status"), Literal.of("active"))))))
                .build();

        AstToPreparedStatementSpecVisitor specFactory = AstToPreparedStatementSpecVisitor.builder()
                .escapeStrategy(new MysqlEscapeStrategy())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();
        assertThatThrownBy(() -> strategy.handle(stmt, specFactory, new AstContext()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MySQL MERGE requires a WHEN NOT MATCHED THEN INSERT clause");
    }
}
