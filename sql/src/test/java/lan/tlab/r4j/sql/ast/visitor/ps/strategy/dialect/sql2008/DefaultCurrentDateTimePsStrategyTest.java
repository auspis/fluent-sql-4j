package lan.tlab.r4j.sql.ast.visitor.ps.strategy.dialect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class DefaultCurrentDateTimePsStrategyTest {

    @Test
    void returnsCurrentTimestampWithoutParameters() {
        var strategy = new DefaultCurrentDateTimePsStrategy();
        var currentDateTime = new CurrentDateTime();
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(currentDateTime, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CURRENT_TIMESTAMP");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesCurrentDateTimeFunction() {
        var strategy = new DefaultCurrentDateTimePsStrategy();
        var currentDateTime = new CurrentDateTime();
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(currentDateTime, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CURRENT_TIMESTAMP");
        assertThat(result.parameters()).isEmpty();
    }
}
