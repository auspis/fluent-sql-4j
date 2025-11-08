package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dml.statement.DeleteStatement;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class StandardSqlDeleteStatementPsStrategyTest {
    @Test
    void deleteWithWhere() {
        TableExpression table = new TableIdentifier("users");
        Comparison whereExpr = Comparison.eq(ColumnReference.of("", "id"), Literal.of(42));
        Where where = Where.of(whereExpr);
        DeleteStatement stmt =
                DeleteStatement.builder().table(table).where(where).build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto ps = renderer.visit(stmt, new AstContext());
        Assertions.assertThat(ps.sql())
                .contains("DELETE FROM")
                .contains("users")
                .contains("WHERE");
        Assertions.assertThat(ps.parameters()).containsExactly(42);
    }

    @Test
    void deleteWithoutWhere() {
        TableExpression table = new TableIdentifier("users");
        DeleteStatement stmt = DeleteStatement.builder().table(table).build();
        PreparedStatementRenderer renderer = new PreparedStatementRenderer();
        PsDto ps = renderer.visit(stmt, new AstContext());
        Assertions.assertThat(ps.sql()).isEqualTo("DELETE FROM users");
        Assertions.assertThat(ps.parameters()).isEmpty();
    }
}
