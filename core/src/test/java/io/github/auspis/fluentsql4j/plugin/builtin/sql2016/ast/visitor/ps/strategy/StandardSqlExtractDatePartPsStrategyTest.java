package io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.Test;
import io.github.auspis.fluentsql4j.ast.core.expression.function.datetime.ExtractDatePart;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlExtractDatePartPsStrategy;

class StandardSqlExtractDatePartPsStrategyTest {

    @Test
    void handlesExtractYearFromColumn() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractYear = ExtractDatePart.year(ColumnReference.of("orders", "created_date"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(extractYear, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(YEAR FROM \"created_date\")");
        assertThat(result.parameters()).isEqualTo(List.of());
    }

    @Test
    void handlesExtractMonthFromLiteral() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractMonth = ExtractDatePart.month(Literal.of("2023-12-25"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(extractMonth, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(MONTH FROM ?)");
        assertThat(result.parameters()).containsExactly("2023-12-25");
    }

    @Test
    void handlesExtractDayFromColumn() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractDay = ExtractDatePart.day(ColumnReference.of("events", "event_date"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(extractDay, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(DAY FROM \"event_date\")");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void handlesExtractWithComplexDateExpression() {
        var strategy = new StandardSqlExtractDatePartPsStrategy();
        var extractYear = ExtractDatePart.year(Literal.of("2023-06-15"));
        var visitor = new AstToPreparedStatementSpecVisitor();
        var ctx = new AstContext();

        PreparedStatementSpec result = strategy.handle(extractYear, visitor, ctx);

        assertThat(result.sql()).isEqualTo("EXTRACT(YEAR FROM ?)");
        assertThat(result.parameters()).containsExactly("2023-06-15");
    }
}
