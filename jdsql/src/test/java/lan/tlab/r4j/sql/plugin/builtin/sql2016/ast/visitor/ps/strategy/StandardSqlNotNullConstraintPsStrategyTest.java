package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.ddl.definition.ConstraintDefinition.NotNullConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.NotNullConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlNotNullConstraintPsStrategyTest {

    private final NotNullConstraintPsStrategy strategy = new StandardSqlNotNullConstraintPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void notNullConstraint() {
        NotNullConstraintDefinition constraint = new NotNullConstraintDefinition();

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).isEqualTo("NOT NULL");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        NotNullConstraintDefinition constraint1 = new NotNullConstraintDefinition();
        NotNullConstraintDefinition constraint2 = new NotNullConstraintDefinition();

        PsDto result1 = strategy.handle(constraint1, renderer, ctx);
        PsDto result2 = strategy.handle(constraint2, renderer, ctx);

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

        PsDto result1 = strategy.handle(constraint, renderer, ctx1);
        PsDto result2 = strategy.handle(constraint, renderer, ctx2);

        assertThat(result1.sql()).isEqualTo("NOT NULL");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("NOT NULL");
        assertThat(result2.parameters()).isEmpty();
        assertThat(result1.sql()).isEqualTo(result2.sql());
    }
}
