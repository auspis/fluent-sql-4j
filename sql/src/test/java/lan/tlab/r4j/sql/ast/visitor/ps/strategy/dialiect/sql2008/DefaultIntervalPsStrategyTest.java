package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultIntervalPsStrategyTest {

    @Test
    void handlesIntervalWithLiteral() {
        var strategy = new DefaultIntervalPsStrategy();
        var interval = Interval.of(Literal.of(30), Interval.IntervalUnit.DAY);
        var visitor = new PreparedStatementVisitor();
        var ctx = new AstContext();

        PsDto result = strategy.handle(interval, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INTERVAL ? DAY");
        assertThat(result.parameters()).containsExactly(30);
    }
}
