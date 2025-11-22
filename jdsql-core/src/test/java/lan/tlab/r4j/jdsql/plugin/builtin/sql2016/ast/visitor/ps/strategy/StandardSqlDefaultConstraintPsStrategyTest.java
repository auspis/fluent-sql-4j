package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlDefaultConstraintPsStrategyTest {

    private final DefaultConstraintPsStrategy strategy = new StandardSqlDefaultConstraintPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void defaultConstraintWithStringValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of("default-value"));

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT 'default-value'");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultConstraintWithNumericValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of(42));

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT 42");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void defaultConstraintWithNullValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.ofNull());

        PsDto result = strategy.handle(constraint, renderer, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT null");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void multipleInstancesSameResult() {
        DefaultConstraintDefinition constraint1 = new DefaultConstraintDefinition(Literal.of("test"));
        DefaultConstraintDefinition constraint2 = new DefaultConstraintDefinition(Literal.of("test"));

        PsDto result1 = strategy.handle(constraint1, renderer, ctx);
        PsDto result2 = strategy.handle(constraint2, renderer, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
