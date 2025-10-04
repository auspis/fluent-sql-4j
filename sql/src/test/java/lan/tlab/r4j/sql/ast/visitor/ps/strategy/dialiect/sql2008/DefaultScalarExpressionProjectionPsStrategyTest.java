package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.clause.selection.projection.ScalarExpressionProjection;
import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultScalarExpressionProjectionPsStrategyTest {

    private DefaultScalarExpressionProjectionPsStrategy strategy;

    @AllArgsConstructor
    static class StubScalarExpression implements ScalarExpression {

        private final PsDto result;

        @SuppressWarnings("unchecked")
        @Override
        public <T> T accept(Visitor<T> visitor, AstContext ctx) {
            return (T) result;
        }
    }

    @BeforeEach
    public void beforeEach() {
        strategy = new DefaultScalarExpressionProjectionPsStrategy();
    }

    @Test
    void noAlias() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("foo", List.of(1)));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr);
        PsDto dto = strategy.handle(projection, null, new AstContext());
        assertThat(dto.sql()).isEqualTo("foo");
        assertThat(dto.parameters()).containsExactly(1);
    }

    @Test
    void alias() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("bar", List.of()));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new As("aliasName"));
        PsDto dto = strategy.handle(projection, null, new AstContext());
        assertThat(dto.sql()).isEqualTo("bar AS \"aliasName\"");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void doesNotAddAliasIfAliasIsBlank() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("baz", List.of("x")));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new As("   "));
        PsDto dto = strategy.handle(projection, null, new AstContext());
        assertThat(dto.sql()).isEqualTo("baz");
        assertThat(dto.parameters()).containsExactly("x");
    }

    @Test
    void propagatesParametersFromExpression() {
        ScalarExpression expr = new StubScalarExpression(new PsDto("col", List.of(42, "foo")));
        ScalarExpressionProjection projection = new ScalarExpressionProjection(expr, new As("a"));
        PsDto dto = strategy.handle(projection, null, new AstContext());
        assertThat(dto.sql()).isEqualTo("col AS \"a\"");
        assertThat(dto.parameters()).containsExactly(42, "foo");
    }
}
