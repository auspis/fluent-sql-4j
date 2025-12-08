package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.core.expression.function.datetime.CurrentDateTime;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import org.junit.jupiter.api.Test;

class StandardSqlCurrentDateTimePsStrategyTest {

    @Test
    void returnsCurrentTimestampWithoutParameters() {
        var strategy = new StandardSqlCurrentDateTimePsStrategy();
        var currentDateTime = new CurrentDateTime();
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(currentDateTime, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CURRENT_TIMESTAMP");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCurrentDateTimeFunction() {
        var strategy = new StandardSqlCurrentDateTimePsStrategy();
        var currentDateTime = new CurrentDateTime();
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(currentDateTime, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CURRENT_TIMESTAMP");
        assertThat(result.parameters()).isEmpty();
    }
}
