package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.common.predicate.Comparison;
import lan.tlab.r4j.sql.ast.dml.component.UpdateItem;
import lan.tlab.r4j.sql.ast.dml.statement.UpdateStatement;
import lan.tlab.r4j.sql.ast.dql.clause.Where;
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
