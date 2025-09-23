package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.set.NullSetExpression;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultNullSetExpressionPsStrategyTest {

    @Test
    void shouldReturnEmptyStringWithNoParameters() {
        // given
        var strategy = new DefaultNullSetExpressionPsStrategy();
        var expression = new NullSetExpression();

        // when
        PsDto result = strategy.handle(expression, null, null);

        // then
        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }
}
