package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.set.ExceptExpression;
import lan.tlab.r4j.sql.ast.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultExceptExpressionPsStrategyTest {

    private final DefaultExceptExpressionPsStrategy strategy = new DefaultExceptExpressionPsStrategy();

    @Test
    void shouldHandleExceptDistinct() {
        // given
        var expression = ExceptExpression.except(new NullSetExpression(), new NullSetExpression());
        var visitor = PreparedStatementVisitor.builder().build();

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
        var visitor = PreparedStatementVisitor.builder().build();

        // when
        PsDto result = strategy.handle(expression, visitor, null);

        // then
        assertThat(result.sql()).isEqualTo("(() EXCEPT ALL ())");
        assertThat(result.parameters()).isEmpty();
    }
}
