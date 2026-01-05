package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.set.IntersectExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.NullSetExpression;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIntersectExpressionPsStrategy;

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
