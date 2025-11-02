package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.conditional.where.Where;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.predicate.Comparison;
import lan.tlab.r4j.sql.ast.statement.dml.UpdateStatement;
import lan.tlab.r4j.sql.ast.statement.dml.item.UpdateItem;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.UpdateStatementPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlUpdateStatementPsStrategyTest {
    @Test
    void updateStatement_generatesParametrizedSqlAndParams() {
        UpdateStatement stmt = UpdateStatement.builder()
                .table(new TableIdentifier("person"))
                .set(List.of(UpdateItem.of("name", Literal.of("Mario")), UpdateItem.of("age", Literal.of(42))))
                .where(Where.of(Comparison.eq(ColumnReference.of("", "id"), Literal.of(1))))
                .build();
        PreparedStatementRenderer renderer = PreparedStatementRenderer.builder().build();
        UpdateStatementPsStrategy strategy = new StandardSqlUpdateStatementPsStrategy();
        PsDto result = strategy.handle(stmt, renderer, new AstContext());
        assertThat(result.sql()).isEqualTo("UPDATE \"person\" SET \"name\" = ?, \"age\" = ? WHERE \"id\" = ?");
        assertThat(result.parameters()).containsExactly("Mario", 42, 1);
    }
}
