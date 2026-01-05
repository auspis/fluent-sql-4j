package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.set.ExceptExpression;
import io.github.auspis.fluentsql4j.ast.core.expression.set.NullSetExpression;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlExceptExpressionPsStrategy;

class StandardSqlExceptExpressionPsStrategyTest {

    private final ExceptExpressionPsStrategy strategy = new StandardSqlExceptExpressionPsStrategy();

    @Test
    void shouldHandleExceptDistinct() {
        // given
        var expression = ExceptExpression.except(new NullSetExpression(), new NullSetExpression());
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();

        // when
        PreparedStatementSpec result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() EXCEPT ())");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleExceptAll() {
        // given
        var expression = ExceptExpression.exceptAll(new NullSetExpression(), new NullSetExpression());
        var visitor = AstToPreparedStatementSpecVisitor.builder().build();

        // when
        PreparedStatementSpec result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() EXCEPT ALL ())");
        assertThat(result.parameters()).isEmpty();
    }
}
