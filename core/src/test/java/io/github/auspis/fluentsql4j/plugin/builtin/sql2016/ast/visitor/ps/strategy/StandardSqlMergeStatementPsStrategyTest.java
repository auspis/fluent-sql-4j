package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.dml.component.InsertData;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenMatchedUpdate;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeAction.WhenNotMatchedInsert;
import io.github.auspis.fluentsql4j.ast.dml.component.MergeUsing;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dml.statement.MergeStatement;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.MergeStatementPsStrategy;
import java.util.List;
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

        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        MergeStatementPsStrategy strategy = new StandardSqlMergeStatementPsStrategy();
        PreparedStatementSpec result = strategy.handle(stmt, specFactory, new AstContext());

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

        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        MergeStatementPsStrategy strategy = new StandardSqlMergeStatementPsStrategy();
        PreparedStatementSpec result = strategy.handle(stmt, specFactory, new AstContext());

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

        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        MergeStatementPsStrategy strategy = new StandardSqlMergeStatementPsStrategy();
        PreparedStatementSpec result = strategy.handle(stmt, specFactory, new AstContext());

        assertThat(result.sql())
                .isEqualTo("MERGE INTO \"users\" "
                        + "USING \"users_updates\" AS src "
                        + "ON \"users\".\"id\" = ? "
                        + "WHEN MATCHED THEN UPDATE SET \"status\" = ? "
                        + "WHEN NOT MATCHED THEN INSERT (\"id\", \"status\") VALUES (?, ?)");
        assertThat(result.parameters()).containsExactly(1, "active", 2, "pending");
    }
}
