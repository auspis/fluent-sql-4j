package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCurrentDatePsStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCurrentDatePsStrategyTest {

    private StandardSqlCurrentDatePsStrategy strategy;
    private PreparedStatementRenderer visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCurrentDatePsStrategy();
        visitor = new PreparedStatementRenderer();
        ctx = new AstContext();
    }

    @Test
    void handleCurrentDate() {
        // Given
        CurrentDate currentDate = new CurrentDate();

        // When
        PsDto result = strategy.handle(currentDate, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CURRENT_DATE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleCurrentDateMultipleCalls() {
        // Given
        CurrentDate currentDate = new CurrentDate();

        // When - multiple calls should return same result
        PsDto result1 = strategy.handle(currentDate, visitor, ctx);
        PsDto result2 = strategy.handle(currentDate, visitor, ctx);

        // Then
        assertThat(result1.sql()).isEqualTo("CURRENT_DATE");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("CURRENT_DATE");
        assertThat(result2.parameters()).isEmpty();
    }
}
