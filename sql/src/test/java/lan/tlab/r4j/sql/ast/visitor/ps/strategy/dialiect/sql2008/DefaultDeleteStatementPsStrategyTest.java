package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.DeleteStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class DefaultDeleteStatementPsStrategyTest {
    @Test
    void deleteWithWhere() {
        TableExpression table = new Table("users");
        Comparison whereExpr = Comparison.eq(ColumnReference.of("", "id"), Literal.of(42));
        Where where = Where.builder().condition(whereExpr).build();
        DeleteStatement stmt =
                DeleteStatement.builder().table(table).where(where).build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto ps = visitor.visit(stmt, new AstContext());
        Assertions.assertThat(ps.sql())
                .contains("DELETE FROM")
                .contains("users")
                .contains("WHERE");
        Assertions.assertThat(ps.parameters()).containsExactly(42);
    }

    @Test
    void deleteWithoutWhere() {
        TableExpression table = new Table("users");
        DeleteStatement stmt = DeleteStatement.builder().table(table).build();
        PreparedStatementVisitor visitor = new PreparedStatementVisitor();
        PsDto ps = visitor.visit(stmt, new AstContext());
        Assertions.assertThat(ps.sql()).isEqualTo("DELETE FROM users");
        Assertions.assertThat(ps.parameters()).isEmpty();
    }
}
