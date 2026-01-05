package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.CurrentDate;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCurrentDatePsStrategy;

class StandardSqlCurrentDatePsStrategyTest {

    private StandardSqlCurrentDatePsStrategy strategy;
    private AstToPreparedStatementSpecVisitor visitor;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCurrentDatePsStrategy();
        visitor = new AstToPreparedStatementSpecVisitor();
        ctx = new AstContext();
    }

    @Test
    void handleCurrentDate() {
        // Given
        CurrentDate currentDate = new CurrentDate();

        // When
        PreparedStatementSpec result = strategy.handle(currentDate, visitor, ctx);

        // Then
        assertThat(result.sql()).isEqualTo("CURRENT_DATE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handleCurrentDateMultipleCalls() {
        // Given
        CurrentDate currentDate = new CurrentDate();

        // When - multiple calls should return same result
        PreparedStatementSpec result1 = strategy.handle(currentDate, visitor, ctx);
        PreparedStatementSpec result2 = strategy.handle(currentDate, visitor, ctx);

        // Then
        assertThat(result1.sql()).isEqualTo("CURRENT_DATE");
        assertThat(result1.parameters()).isEmpty();
        assertThat(result2.sql()).isEqualTo("CURRENT_DATE");
        assertThat(result2.parameters()).isEmpty();
    }
}
