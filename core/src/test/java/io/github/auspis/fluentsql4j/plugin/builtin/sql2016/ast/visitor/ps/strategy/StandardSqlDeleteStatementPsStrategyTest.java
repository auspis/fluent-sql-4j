package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.expression.set.TableExpression;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.dml.statement.DeleteStatement;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;

class StandardSqlDeleteStatementPsStrategyTest {
    @Test
    void deleteWithWhere() {
        TableExpression table = new TableIdentifier("users");
        Comparison whereExpr = Comparison.eq(ColumnReference.of("", "id"), Literal.of(42));
        Where where = Where.of(whereExpr);
        DeleteStatement stmt =
                DeleteStatement.builder().table(table).where(where).build();
        AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
        PreparedStatementSpec ps = specFactory.visit(stmt, new AstContext());
        Assertions.assertThat(ps.sql())
                .contains("DELETE FROM")
                .contains("\"users\"")
                .contains("WHERE");
        Assertions.assertThat(ps.parameters()).containsExactly(42);
    }

    @Test
    void deleteWithoutWhere() {
        TableExpression table = new TableIdentifier("users");
        DeleteStatement stmt = DeleteStatement.builder().table(table).build();
        AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
        PreparedStatementSpec ps = specFactory.visit(stmt, new AstContext());
        Assertions.assertThat(ps.sql()).isEqualTo("DELETE FROM \"users\"");
        Assertions.assertThat(ps.parameters()).isEmpty();
    }
}
