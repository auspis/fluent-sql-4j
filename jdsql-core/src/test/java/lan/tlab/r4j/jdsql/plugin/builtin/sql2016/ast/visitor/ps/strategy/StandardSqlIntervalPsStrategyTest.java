package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlIntervalPsStrategyTest {

    @Test
    void handlesIntervalWithLiteral() {
        var strategy = new StandardSqlIntervalPsStrategy();
        var interval = new Interval(Literal.of(30), Interval.IntervalUnit.DAY);
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(interval, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INTERVAL ? DAY");
        assertThat(result.parameters()).containsExactly(30);
    }
}
