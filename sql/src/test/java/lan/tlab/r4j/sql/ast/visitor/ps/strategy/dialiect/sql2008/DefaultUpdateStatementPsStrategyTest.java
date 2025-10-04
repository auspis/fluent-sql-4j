package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.expression.item.UpdateItem;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.UpdateStatement;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultUpdateStatementPsStrategyTest {
    @Test
    void updateStatement_generatesParametrizedSqlAndParams() {
        UpdateStatement stmt = UpdateStatement.builder()
                .table(new Table("person"))
                .set(List.of(UpdateItem.of("name", Literal.of("Mario")), UpdateItem.of("age", Literal.of(42))))
                .where(Where.of(Comparison.eq(ColumnReference.of("", "id"), Literal.of(1))))
                .build();
        PreparedStatementVisitor visitor = PreparedStatementVisitor.builder().build();
        DefaultUpdateStatementPsStrategy strategy = new DefaultUpdateStatementPsStrategy();
        PsDto result = strategy.handle(stmt, visitor, new AstContext());
        assertThat(result.sql()).isEqualTo("UPDATE \"person\" SET \"name\" = ?, \"age\" = ? WHERE \"id\" = ?");
        assertThat(result.parameters()).containsExactly("Mario", 42, 1);
    }
}
