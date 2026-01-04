package io.github.massimiliano.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.massimiliano.fluentsql4j.ast.core.expression.set.NullSetExpression;
import io.github.massimiliano.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

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
