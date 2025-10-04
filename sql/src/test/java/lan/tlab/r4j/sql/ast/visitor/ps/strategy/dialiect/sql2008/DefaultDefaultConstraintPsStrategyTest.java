package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.item.ddl.Constraint.DefaultConstraint;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
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
        DefaultConstraint constraint = new DefaultConstraint(Literal.of("default-value"));

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT 'default-value'");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultConstraintWithNumericValue() {
        DefaultConstraint constraint = new DefaultConstraint(Literal.of(42));

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT 42");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultConstraintWithNullValue() {
        DefaultConstraint constraint = new DefaultConstraint(Literal.ofNull());

        PsDto result = strategy.handle(constraint, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT null");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        DefaultConstraint constraint1 = new DefaultConstraint(Literal.of("test"));
        DefaultConstraint constraint2 = new DefaultConstraint(Literal.of("test"));

        PsDto result1 = strategy.handle(constraint1, visitor, ctx);
        PsDto result2 = strategy.handle(constraint2, visitor, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
