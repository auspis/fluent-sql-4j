package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.set.NullSetExpression;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class StandardSqlNullSetExpressionPsStrategyTest {

    @Test
    void shouldReturnEmptyStringWithNoParameters() {
        // given
        var strategy = new StandardSqlNullSetExpressionPsStrategy();
        var expression = new NullSetExpression();

        // when
        PsDto result = strategy.handle(expression, null, null);

        // then
        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }
}
