package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlExtractDatePartRenderStrategyTest {

    private StandardSqlExtractDatePartRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlExtractDatePartRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void year_columnReference() {
        ExtractDatePart func = ExtractDatePart.year(ColumnReference.of("Customer", "birthdate"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("YEAR(\"Customer\".\"birthdate\")");
    }

    @Test
    void month_columnReference() {
        ExtractDatePart func = ExtractDatePart.month(ColumnReference.of("Customer", "birthdate"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MONTH(\"Customer\".\"birthdate\")");
    }

    @Test
    void day_columnReference() {
        ExtractDatePart func = ExtractDatePart.day(ColumnReference.of("Customer", "birthdate"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("DAY(\"Customer\".\"birthdate\")");
    }

    @Test
    void year_literal() {
        ExtractDatePart func = ExtractDatePart.year(Literal.of("2023-01-15"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("YEAR('2023-01-15')");
    }

    @Test
    void month_literal() {
        ExtractDatePart func = ExtractDatePart.month(Literal.of("2023-01-15"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("MONTH('2023-01-15')");
    }

    @Test
    void year_expression() {
        ScalarExpression currentTs = new CurrentDateTime();
        ExtractDatePart func = ExtractDatePart.year(currentTs);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("YEAR(CURRENT_TIMESTAMP())");
    }

    @Test
    void day_expression() {
        ScalarExpression currentTs = new CurrentDateTime();
        ExtractDatePart func = ExtractDatePart.day(currentTs);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("DAY(CURRENT_TIMESTAMP())");
    }
}
