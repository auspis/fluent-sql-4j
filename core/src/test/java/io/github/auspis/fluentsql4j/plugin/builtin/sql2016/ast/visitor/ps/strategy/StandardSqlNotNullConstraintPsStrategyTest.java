package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlNotNullConstraintPsStrategyTest {

    private final NotNullConstraintPsStrategy strategy = new StandardSqlNotNullConstraintPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void notNullConstraint() {
        NotNullConstraintDefinition constraint = new NotNullConstraintDefinition();

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        NotNullConstraintDefinition constraint1 = new NotNullConstraintDefinition();
        NotNullConstraintDefinition constraint2 = new NotNullConstraintDefinition();

        PreparedStatementSpec result1 = strategy.handle(constraint1, specFactory, ctx);
        PreparedStatementSpec result2 = strategy.handle(constraint2, specFactory, ctx);

        assertThat(result1.sql()).isEqualTo("NOT NULL");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("NOT NULL");
        assertThat(result2.parameters()).isEmpty();
        assertThat(result1.sql()).isEqualTo(result2.sql());
    }

    @Test
    void constraintWithDifferentContexts() {
        NotNullConstraintDefinition constraint = new NotNullConstraintDefinition();
        AstContext ctx1 = new AstContext();
        AstContext ctx2 = new AstContext();

        PreparedStatementSpec result1 = strategy.handle(constraint, specFactory, ctx1);
        PreparedStatementSpec result2 = strategy.handle(constraint, specFactory, ctx2);

        assertThat(result1.sql()).isEqualTo("NOT NULL");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("NOT NULL");
        assertThat(result2.parameters()).isEmpty();
        assertThat(result1.sql()).isEqualTo(result2.sql());
    }
}
