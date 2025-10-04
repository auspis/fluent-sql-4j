package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultNullScalarExpressionPsStrategyTest {

    @Test
    void handlesNullScalarExpression() {
        var strategy = new DefaultNullScalarExpressionPsStrategy();
        var nullExpression = new NullScalarExpression();
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(nullExpression, visitor, ctx);

        assertThat(result.sql()).isEqualTo("NULL");
        assertThat(result.parameters()).isEmpty();
    }
}
