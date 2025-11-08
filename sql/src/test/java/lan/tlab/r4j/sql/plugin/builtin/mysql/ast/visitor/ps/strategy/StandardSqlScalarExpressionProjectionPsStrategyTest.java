package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.identifier.Alias;
import lan.tlab.r4j.sql.ast.dql.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlScalarExpressionProjectionPsStrategyTest {

    private StandardSqlScalarExpressionProjectionPsStrategy strategy;
    private PreparedStatementRenderer renderer;

    static record StubScalarExpression(PsDto result) implements ScalarExpression {

        @SuppressWarnings("unchecked")
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return (T) result;
        }
    }

    @BeforeEach
    public void beforeEach() {
        strategy = new StandardSqlScalarExpressionProjectionPsStrategy();
        renderer = new PreparedStatementRenderer();
    }

    @Test
    void noAlias() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("foo", List.of(1)));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr);
        PsDto dto = strategy.handle(projection, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("foo");
        assertThat(dto.parameters()).containsExactly(1);
    }

    @Test
    void alias() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("bar", List.of()));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new Alias("aliasName"));
        PsDto dto = strategy.handle(projection, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("bar AS \"aliasName\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void doesNotAddAliasIfAliasIsBlank() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("baz", List.of("x")));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new Alias("   "));
        PsDto dto = strategy.handle(projection, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("baz");
        assertThat(dto.parameters()).containsExactly("x");
    }

    @Test
    void propagatesParametersFromExpression() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("col", List.of(42, "foo")));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new Alias("a"));
        PsDto dto = strategy.handle(projection, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("col AS \"a\"");
        assertThat(dto.parameters()).containsExactly(42, "foo");
    }
}
