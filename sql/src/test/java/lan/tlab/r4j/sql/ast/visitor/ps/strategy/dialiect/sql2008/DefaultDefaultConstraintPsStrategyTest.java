package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class DefaultDefaultConstraintPsStrategyTest {

    private final DefaultConstraintPsStrategy strategy = new DefaultDefaultConstraintPsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void defaultConstraintWithStringValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of("default-value"));

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT 'default-value'");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultConstraintWithNumericValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of(42));

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT 42");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultConstraintWithNullValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.ofNull());

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT null");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        DefaultConstraintDefinition constraint1 = new DefaultConstraintDefinition(Literal.of("test"));
        DefaultConstraintDefinition constraint2 = new DefaultConstraintDefinition(Literal.of("test"));

        PsDto result1 = strategy.handle(constraint1, visitor, ctx);
        PsDto result2 = strategy.handle(constraint2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
