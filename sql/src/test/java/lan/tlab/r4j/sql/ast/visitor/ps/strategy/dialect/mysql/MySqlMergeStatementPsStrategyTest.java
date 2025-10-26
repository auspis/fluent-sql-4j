package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.mysql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.dml.MergeStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.InsertData;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.sql.ast.statement.dml.item.MergeUsing;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.escape.EscapeStrategy;
import org.junit.jupiter.api.Test;

class MySqlMergeStatementPsStrategyTest {

    @Test
    void mergeWithMatchedAndNotMatched_generatesParametrizedMySqlSyntax() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users", "tgt"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("tgt", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(
                        new WhenMatchedUpdate(List.of(
                                UpdateItem.builder()
                                        .column(ColumnReference.of("", "name"))
                                        .value(ColumnReference.of("src", "name"))
                                        .build(),
                                UpdateItem.builder()
                                        .column(ColumnReference.of("", "status"))
                                        .value(Literal.of("updated"))
                                        .build())),
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

        SqlRenderer sqlRenderer =
                SqlRenderer.builder().escapeStrategy(EscapeStrategy.mysql()).build();
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .sqlRenderer(sqlRenderer)
                .escapeStrategy(EscapeStrategy.mysql())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();
        PsDto result = strategy.handle(stmt, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo("INSERT INTO `users` (`id`, `name`, `status`) "
                        + "SELECT `src`.`id`, `src`.`name`, ? "
                        + "FROM `users_updates` AS src "
                        + "ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `status` = ?");
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

        SqlRenderer sqlRenderer =
                SqlRenderer.builder().escapeStrategy(EscapeStrategy.mysql()).build();
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .sqlRenderer(sqlRenderer)
                .escapeStrategy(EscapeStrategy.mysql())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();
        PsDto result = strategy.handle(stmt, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo("INSERT INTO `users` (`id`, `name`) "
                        + "SELECT `src`.`id`, ? "
                        + "FROM `users_updates` AS src");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void mergeWithLiterals_generatesParametrizedSql() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), Literal.of(1)))
                .actions(List.of(
                        new WhenMatchedUpdate(List.of(UpdateItem.builder()
                                .column(ColumnReference.of("", "status"))
                                .value(Literal.of("active"))
                                .build())),
                        new WhenNotMatchedInsert(
                                List.of(ColumnReference.of("", "id"), ColumnReference.of("", "status")),
                                InsertData.InsertValues.of(Literal.of(2), Literal.of("pending")))))
                .build();

        SqlRenderer sqlRenderer =
                SqlRenderer.builder().escapeStrategy(EscapeStrategy.mysql()).build();
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .sqlRenderer(sqlRenderer)
                .escapeStrategy(EscapeStrategy.mysql())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();
        PsDto result = strategy.handle(stmt, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo("INSERT INTO `users` (`id`, `status`) "
                        + "SELECT ?, ? "
                        + "FROM `users_updates` AS src "
                        + "ON DUPLICATE KEY UPDATE `status` = ?");
        assertThat(result.parameters()).containsExactly(2, "pending", "active");
    }

    @Test
    void missingWhenNotMatchedThrowsException() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(new WhenMatchedUpdate(List.of(UpdateItem.builder()
                        .column(ColumnReference.of("", "status"))
                        .value(Literal.of("active"))
                        .build()))))
                .build();

        SqlRenderer sqlRenderer =
                SqlRenderer.builder().escapeStrategy(EscapeStrategy.mysql()).build();
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder()
                .sqlRenderer(sqlRenderer)
                .escapeStrategy(EscapeStrategy.mysql())
                .build();
        MySqlMergeStatementPsStrategy strategy = new MySqlMergeStatementPsStrategy();

        assertThatThrownBy(() -> strategy.handle(stmt, renderer, new AstContext()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MySQL MERGE requires a WHEN NOT MATCHED THEN INSERT clause");
    }
}
