package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.bool.NullBooleanExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import org.junit.jupiter.api.Test;

class DefaultNullBooleanExpressionPsStrategyTest {

    @Test
    void handleNullBooleanExpression() {
        var expression = new NullBooleanExpression();

        var strategy = new DefaultNullBooleanExpressionPsStrategy();
        var visitor = new PreparedStatementVisitor();
        var result = strategy.handle(expression, visitor, new AstContext());

        assertThat(result.sql()).isEqualTo("NULL");
        assertThat(result.parameters()).isEmpty();
    }
}
