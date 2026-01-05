package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.set.NullSetExpression;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlNullSetExpressionPsStrategy;

class StandardSqlNullSetExpressionPsStrategyTest {

    @Test
    void shouldReturnEmptyStringWithNoParameters() {
        // given
        var strategy = new StandardSqlNullSetExpressionPsStrategy();
        var expression = new NullSetExpression();

        // when
        PreparedStatementSpec result = strategy.handle(expression, null, null);

        // then
        assertThat(result.sql()).isEmpty();
        assertThat(result.parameters()).isEmpty();
    }
}
