package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.common.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlExceptExpressionPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlExceptExpressionPsStrategyTest {

    private final ExceptExpressionPsStrategy strategy = new StandardSqlExceptExpressionPsStrategy();

    @Test
    void shouldHandleExceptDistinct() {
        // given
        var expression = ExceptExpression.except(new NullSetExpression(), new NullSetExpression());
        var visitor = PreparedStatementRenderer.builder().build();

        // when
        PsDto result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() EXCEPT ())");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void shouldHandleExceptAll() {
        // given
        var expression = ExceptExpression.exceptAll(new NullSetExpression(), new NullSetExpression());
        var visitor = PreparedStatementRenderer.builder().build();

        // when
        PsDto result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() EXCEPT ALL ())");
        assertThat(result.parameters()).isEmpty();
    }
}
