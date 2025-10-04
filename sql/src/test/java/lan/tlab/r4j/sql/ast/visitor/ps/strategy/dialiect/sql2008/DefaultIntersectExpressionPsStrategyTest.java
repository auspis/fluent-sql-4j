package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.set.IntersectExpression;
import lan.tlab.r4j.sql.ast.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultIntersectExpressionPsStrategyTest {

    private final DefaultIntersectExpressionPsStrategy strategy = new DefaultIntersectExpressionPsStrategy();

    @Test
    void shouldHandleIntersectDistinct() {
        // given
        var expression = IntersectExpression.intersect(new NullSetExpression(), new NullSetExpression());
        var visitor = PreparedStatementVisitor.builder().build();

        // when
        PsDto result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() INTERSECT ())");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleIntersectAll() {
        // given
        var expression = IntersectExpression.intersectAll(new NullSetExpression(), new NullSetExpression());
        var visitor = PreparedStatementVisitor.builder().build();

        // when
        PsDto result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() INTERSECT ALL ())");
        assertThat(result.parameters()).isEmpty();
    }
}
