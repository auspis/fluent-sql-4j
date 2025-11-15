package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.interval.Interval;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlIntervalPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlIntervalPsStrategyTest {

    @Test
    void handlesIntervalWithLiteral() {
        var strategy = new StandardSqlIntervalPsStrategy();
        var interval = new Interval(Literal.of(30), Interval.IntervalUnit.DAY);
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(interval, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INTERVAL ? DAY");
        assertThat(result.parameters()).containsExactly(30);
    }
}
