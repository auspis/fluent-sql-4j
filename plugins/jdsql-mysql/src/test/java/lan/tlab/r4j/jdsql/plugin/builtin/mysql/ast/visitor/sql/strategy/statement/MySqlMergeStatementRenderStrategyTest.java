package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.jdsql.ast.common.predicate.Comparison;
import lan.tlab.r4j.jdsql.ast.dml.component.InsertData;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenMatchedUpdate;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import lan.tlab.r4j.jdsql.ast.dml.component.MergeUsing;
import lan.tlab.r4j.jdsql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.jdsql.ast.dml.statement.MergeStatement;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.mysql.MysqlSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlMergeStatementRenderStrategyTest {

    private MySqlMergeStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new MySqlMergeStatementRenderStrategy();
        renderer = MysqlSqlRendererFactory.create();
    }

    @Test
    void mergeWithMatchedAndNotMatched() {
        MergeStatement statement = MergeStatement.builder()
                .targetTable(new TableIdentifier("users", "tgt"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("tgt", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(
                        new WhenMatchedUpdate(List.of(
                                new UpdateItem(ColumnReference.of("", "name"), ColumnReference.of("src", "name")),
                                new UpdateItem(ColumnReference.of("", "email"), ColumnReference.of("src", "email")))),
                        new WhenNotMatchedInsert(
                                List.of(
                                        ColumnReference.of("", "id"),
                                        ColumnReference.of("", "name"),
                                        ColumnReference.of("", "email")),
                                InsertData.InsertValues.of(
                                        ColumnReference.of("src", "id"),
                                        ColumnReference.of("src", "name"),
                                        ColumnReference.of("src", "email")))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO `users` (`id`, `name`, `email`) \
                SELECT `src`.`id`, `src`.`name`, `src`.`email` \
                FROM `users_updates` AS src \
                ON DUPLICATE KEY UPDATE `name` = VALUES(`name`), `email` = VALUES(`email`)\
                """);
    }

    @Test
    void mergeWithOnlyNotMatched() {
        MergeStatement statement = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(new WhenNotMatchedInsert(
                        List.of(ColumnReference.of("", "id"), ColumnReference.of("", "name")),
                        InsertData.InsertValues.of(
                                ColumnReference.of("src", "id"), ColumnReference.of("src", "name")))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO `users` (`id`, `name`) \
                SELECT `src`.`id`, `src`.`name` \
                FROM `users_updates` AS src\
                """);
    }

    @Test
    void mergeWithLiterals() {
        MergeStatement statement = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(
                        new WhenMatchedUpdate(
                                List.of(new UpdateItem(ColumnReference.of("", "status"), Literal.of("active")))),
                        new WhenNotMatchedInsert(
                                List.of(ColumnReference.of("", "id"), ColumnReference.of("", "status")),
                                InsertData.InsertValues.of(ColumnReference.of("src", "id"), Literal.of("pending")))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO `users` (`id`, `status`) \
                SELECT `src`.`id`, 'pending' \
                FROM `users_updates` AS src \
                ON DUPLICATE KEY UPDATE `status` = 'active'\
                """);
    }

    @Test
    void missingWhenNotMatchedThrowsException() {
        MergeStatement statement = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(new WhenMatchedUpdate(
                        List.of(new UpdateItem(ColumnReference.of("", "status"), Literal.of("active"))))))
                .build();

        assertThatThrownBy(() -> strategy.render(statement, renderer, new AstContext()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("MySQL MERGE requires a WHEN NOT MATCHED THEN INSERT clause");
    }

    @Test
    void updateWithLiteralValue() {
        MergeStatement statement = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(
                        new WhenMatchedUpdate(List.of(
                                new UpdateItem(ColumnReference.of("", "status"), Literal.of("active")),
                                new UpdateItem(ColumnReference.of("", "name"), ColumnReference.of("src", "name")))),
                        new WhenNotMatchedInsert(
                                List.of(ColumnReference.of("", "id"), ColumnReference.of("", "name")),
                                InsertData.InsertValues.of(
                                        ColumnReference.of("src", "id"), ColumnReference.of("src", "name")))))
                .build();

        String sql = strategy.render(statement, renderer, new AstContext());
        assertThat(sql)
                .isEqualTo(
                        """
                INSERT INTO `users` (`id`, `name`) \
                SELECT `src`.`id`, `src`.`name` \
                FROM `users_updates` AS src \
                ON DUPLICATE KEY UPDATE `status` = 'active', `name` = VALUES(`name`)\
                """);
    }
}
