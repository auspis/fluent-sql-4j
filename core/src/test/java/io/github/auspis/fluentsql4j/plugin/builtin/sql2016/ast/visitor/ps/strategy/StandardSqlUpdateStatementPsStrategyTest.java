package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.core.predicate.Comparison;
import io.github.auspis.fluentsql4j.ast.dml.component.UpdateItem;
import io.github.auspis.fluentsql4j.ast.dml.statement.UpdateStatement;
import io.github.auspis.fluentsql4j.ast.dql.clause.Where;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.UpdateStatementPsStrategy;
import java.util.List;
import org.junit.jupiter.api.Test;

class StandardSqlUpdateStatementPsStrategyTest {
    @Test
    void updateStatement_generatesParametrizedSqlAndParams() {
        UpdateStatement stmt = UpdateStatement.builder()
                .table(new TableIdentifier("person"))
                .set(List.of(UpdateItem.of("name", Literal.of("Mario")), UpdateItem.of("age", Literal.of(42))))
                .where(Where.of(Comparison.eq(ColumnReference.of("", "id"), Literal.of(1))))
                .build();
        AstToPreparedStatementSpecVisitor specFactory =
                AstToPreparedStatementSpecVisitor.builder().build();
        UpdateStatementPsStrategy strategy = new StandardSqlUpdateStatementPsStrategy();
        PreparedStatementSpec result = strategy.handle(stmt, specFactory, new AstContext());
        assertThat(result.sql()).isEqualTo("UPDATE \"person\" SET \"name\" = ?, \"age\" = ? WHERE \"id\" = ?");
        assertThat(result.parameters()).containsExactly("Mario", 42, 1);
    }
}
