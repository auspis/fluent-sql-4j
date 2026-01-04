package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.massimiliano.fluentsql4j.ast.ddl.definition.ConstraintDefinition.DefaultConstraintDefinition;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.DefaultConstraintPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlDefaultConstraintPsStrategyTest {

    private final DefaultConstraintPsStrategy strategy = new StandardSqlDefaultConstraintPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void defaultConstraintWithStringValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of("default-value"));

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT ?");
        assertThat(result.parameters()).containsExactly("default-value");
    }

    @Test
    void defaultConstraintWithNumericValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.of(42));

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT ?");
        assertThat(result.parameters()).containsExactly(42);
    }

    @Test
    void defaultConstraintWithNullValue() {
        DefaultConstraintDefinition constraint = new DefaultConstraintDefinition(Literal.ofNull());

        PreparedStatementSpec result = strategy.handle(constraint, specFactory, ctx);

        assertThat(result.sql()).isEqualTo("DEFAULT ?");
        assertThat(result.parameters()).containsExactly((String) null);
    }

    @Test
    void multipleInstancesSameResult() {
        DefaultConstraintDefinition constraint1 = new DefaultConstraintDefinition(Literal.of("test"));
        DefaultConstraintDefinition constraint2 = new DefaultConstraintDefinition(Literal.of("test"));

        PreparedStatementSpec result1 = strategy.handle(constraint1, specFactory, ctx);
        PreparedStatementSpec result2 = strategy.handle(constraint2, specFactory, ctx);

        assertThat(result1.sql()).isEqualTo(result2.sql());
        assertThat(result1.parameters()).isEqualTo(result2.parameters());
    }
}
