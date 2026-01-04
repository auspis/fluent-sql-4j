package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.set.IntersectExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.NullSetExpression;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlIntersectExpressionPsStrategyTest {

    private final IntersectExpressionPsStrategy strategy = new StandardSqlIntersectExpressionPsStrategy();

    @Test
    void shouldHandleIntersectDistinct() {
        // given
        var expression = IntersectExpression.intersect(new NullSetExpression(), new NullSetExpression());
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();

        // when
        PreparedStatementSpec result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() INTERSECT ())");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleIntersectAll() {
        // given
        var expression = IntersectExpression.intersectAll(new NullSetExpression(), new NullSetExpression());
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();

        // when
        PreparedStatementSpec result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() INTERSECT ALL ())");
        assertThat(result.parameters()).isEmpty();
    }
}
