package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

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
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.MergeStatementPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlMergeStatementPsStrategyTest {

    @Test
    void mergeWithMatchedAndNotMatched_generatesParametrizedSql() {
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

        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        MergeStatementPsStrategy strategy = new StandardSqlMergeStatementPsStrategy();
        PsDto result = strategy.handle(stmt, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo(
                        "MERGE INTO \"users\" AS tgt "
                                + "USING \"users_updates\" AS src "
                                + "ON \"tgt\".\"id\" = \"src\".\"id\" "
                                + "WHEN MATCHED THEN UPDATE SET \"name\" = \"src\".\"name\", \"status\" = ? "
                                + "WHEN NOT MATCHED THEN INSERT (\"id\", \"name\", \"status\") VALUES (\"src\".\"id\", \"src\".\"name\", ?)");
        assertThat(result.parameters()).containsExactly("updated", "new");
    }

    @Test
    void mergeWithOnlyNotMatched_generatesParametrizedSql() {
        MergeStatement stmt = MergeStatement.builder()
                .targetTable(new TableIdentifier("users"))
                .using(new MergeUsing(new TableIdentifier("users_updates", "src")))
                .onCondition(Comparison.eq(ColumnReference.of("users", "id"), ColumnReference.of("src", "id")))
                .actions(List.of(new WhenNotMatchedInsert(
                        List.of(ColumnReference.of("", "id"), ColumnReference.of("", "name")),
                        InsertData.InsertValues.of(ColumnReference.of("src", "id"), Literal.of("John")))))
                .build();

        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        MergeStatementPsStrategy strategy = new StandardSqlMergeStatementPsStrategy();
        PsDto result = strategy.handle(stmt, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo("MERGE INTO \"users\" "
                        + "USING \"users_updates\" AS src "
                        + "ON \"users\".\"id\" = \"src\".\"id\" "
                        + "WHEN NOT MATCHED THEN INSERT (\"id\", \"name\") VALUES (\"src\".\"id\", ?)");
        assertThat(result.parameters()).containsExactly("John");
    }

    @Test
    void mergeWithAllLiterals_generatesParametrizedSql() {
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

        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        MergeStatementPsStrategy strategy = new StandardSqlMergeStatementPsStrategy();
        PsDto result = strategy.handle(stmt, renderer, new AstContext());

        assertThat(result.sql())
                .isEqualTo("MERGE INTO \"users\" "
                        + "USING \"users_updates\" AS src "
                        + "ON \"users\".\"id\" = ? "
                        + "WHEN MATCHED THEN UPDATE SET \"status\" = ? "
                        + "WHEN NOT MATCHED THEN INSERT (\"id\", \"status\") VALUES (?, ?)");
        assertThat(result.parameters()).containsExactly(1, "active", 2, "pending");
    }
}
