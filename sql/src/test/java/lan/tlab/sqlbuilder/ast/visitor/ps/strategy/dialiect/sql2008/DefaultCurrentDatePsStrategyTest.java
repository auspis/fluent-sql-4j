package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DefaultCurrentDatePsStrategyTest {

    private DefaultCurrentDatePsStrategy strategy;
    private PreparedStatementVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new DefaultCurrentDatePsStrategy();
        visitor = new PreparedStatementVisitor();
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
