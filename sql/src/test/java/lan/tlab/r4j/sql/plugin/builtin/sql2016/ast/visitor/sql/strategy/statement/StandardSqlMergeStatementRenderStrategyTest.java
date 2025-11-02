package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.statement;

import static org.assertj.core.api.Assertions.assertThat;

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

class StandardSqlMergeStatementRenderStrategyTest {

    private StandardSqlMergeStatementRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlMergeStatementRenderStrategy();
        renderer = TestDialectRendererFactory.standardSql2008();
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
                MERGE INTO "users" AS tgt \
                USING "users_updates" AS src \
                ON "tgt"."id" = "src"."id" \
                WHEN MATCHED THEN UPDATE SET "name" = "src"."name", "email" = "src"."email" \
                WHEN NOT MATCHED THEN INSERT ("id", "name", "email") VALUES ("src"."id", "src"."name", "src"."email")\
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
                MERGE INTO "users" \
                USING "users_updates" AS src \
                ON "users"."id" = "src"."id" \
                WHEN NOT MATCHED THEN INSERT ("id", "name") VALUES ("src"."id", "src"."name")\
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
                MERGE INTO "users" \
                USING "users_updates" AS src \
                ON "users"."id" = "src"."id" \
                WHEN MATCHED THEN UPDATE SET "status" = 'active' \
                WHEN NOT MATCHED THEN INSERT ("id", "status") VALUES ("src"."id", 'pending')\
                """);
    }
}
