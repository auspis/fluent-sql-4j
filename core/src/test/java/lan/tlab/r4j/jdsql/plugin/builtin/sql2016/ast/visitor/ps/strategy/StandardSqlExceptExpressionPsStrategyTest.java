package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.core.expression.set.ExceptExpression;
import lan.tlab.r4j.jdsql.ast.core.expression.set.NullSetExpression;
import lan.tlab.r4j.jdsql.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.ExceptExpressionPsStrategy;
import org.junit.jupiter.api.Test;

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
