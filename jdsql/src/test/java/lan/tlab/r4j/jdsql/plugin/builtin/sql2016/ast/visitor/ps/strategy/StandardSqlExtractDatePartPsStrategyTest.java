package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.datetime.ExtractDatePart;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PsDto;
import org.junit.jupiter.api.Test;

class StandardSqlExtractDatePartPsStrategyTest {

    @Test
    void handlesExtractYearFromColumn() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractYear = ExtractDatePart.year(ColumnReference.of("orders", "created_date"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(extractYear, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(YEAR FROM \"created_date\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesExtractMonthFromLiteral() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractMonth = ExtractDatePart.month(Literal.of("2023-12-25"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(extractMonth, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(MONTH FROM ?)");
        assertThat(result.parameters()).containsExactly("2023-12-25");
    }

    @Test
    void handlesExtractDayFromColumn() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractDay = ExtractDatePart.day(ColumnReference.of("events", "event_date"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(extractDay, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(DAY FROM \"event_date\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handlesExtractWithComplexDateExpression() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractYear = ExtractDatePart.year(Literal.of("2023-06-15"));
        var visitor = new PreparedStatementRenderer();
        var ctx = new AstContext();

        PsDto result = strategy.handle(extractYear, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(YEAR FROM ?)");
        assertThat(result.parameters()).containsExactly("2023-06-15");
    }
}
