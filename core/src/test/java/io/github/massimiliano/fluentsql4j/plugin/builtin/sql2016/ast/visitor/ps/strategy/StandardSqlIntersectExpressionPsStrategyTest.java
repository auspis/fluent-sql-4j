package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.IntersectExpression;
import io.github.massimiliano.fluentsql4j.ast.core.expression.set.NullSetExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.strategy.IntersectExpressionPsStrategy;
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
