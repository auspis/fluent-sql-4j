package lan.tlab.r4j.sql.ast.visitor.sql.dialect.mysql.strategy.statement;

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
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MySqlMergeStatementRenderStrategyTest {

    private MySqlMergeStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new MySqlMergeStatementRenderStrategy();
        renderer = TestDialectRendererFactory.mysql();
    }

    @Test
    void mergeWithMatchedAndNotMatched() {
        MergeStatement statement = MergeStatement.builder()
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
                                        .column(ColumnReference.of("", "email"))
                                        .value(ColumnReference.of("src", "email"))
                                        .build())),
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
                INSERT INTO `users` AS tgt (`id`, `name`, `email`) \
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
                        new WhenMatchedUpdate(List.of(UpdateItem.builder()
                                .column(ColumnReference.of("", "status"))
                                .value(Literal.of("active"))
                                .build())),
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
                .actions(List.of(new WhenMatchedUpdate(List.of(UpdateItem.builder()
                        .column(ColumnReference.of("", "status"))
                        .value(Literal.of("active"))
                        .build()))))
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
                                UpdateItem.builder()
                                        .column(ColumnReference.of("", "status"))
                                        .value(Literal.of("active"))
                                        .build(),
                                UpdateItem.builder()
                                        .column(ColumnReference.of("", "name"))
                                        .value(ColumnReference.of("src", "name"))
                                        .build())),
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
