package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.Alias;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.ScalarExpressionProjection;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlScalarExpressionProjectionPsStrategyTest {

    private StandardSqlScalarExpressionProjectionPsStrategy strategy;
    private AstToPreparedStatementSpecVisitor specFactory;

    static record StubScalarExpression(PreparedStatementSpec result) implements ScalarExpression {

        @SuppressWarnings("unchecked")
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return (T) result;
        }
    }

    @BeforeEach
    public void beforeEach() {
        strategy = new StandardSqlScalarExpressionProjectionPsStrategy();
        specFactory = new AstToPreparedStatementSpecVisitor();
    }

    @Test
    void noAlias() {
        ScalarExpression expr = new StubScalarExpression(new PreparedStatementSpec("foo", List.of(1)));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr);
        PreparedStatementSpec dto = strategy.handle(projection, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("foo");
        assertThat(dto.parameters()).containsExactly(1);
    }

    @Test
    void alias() {
        ScalarExpression expr = new StubScalarExpression(new PreparedStatementSpec("bar", List.of()));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new Alias("aliasName"));
        PreparedStatementSpec dto = strategy.handle(projection, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("bar AS \"aliasName\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void doesNotAddAliasIfAliasIsBlank() {
        ScalarExpression expr = new StubScalarExpression(new PreparedStatementSpec("baz", List.of("x")));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new Alias("   "));
        PreparedStatementSpec dto = strategy.handle(projection, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("baz");
        assertThat(dto.parameters()).containsExactly("x");
    }

    @Test
    void propagatesParametersFromExpression() {
        ScalarExpression expr = new StubScalarExpression(new PreparedStatementSpec("col", List.of(42, "foo")));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new Alias("a"));
        PreparedStatementSpec dto = strategy.handle(projection, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("col AS \"a\"");
        assertThat(dto.parameters()).containsExactly(42, "foo");
    }
}
