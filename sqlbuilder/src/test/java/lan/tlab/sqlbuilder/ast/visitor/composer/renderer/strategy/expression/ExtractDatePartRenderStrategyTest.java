package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.ExtractDatePart;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExtractDatePartRenderStrategyTest {

    private ExtractDatePartRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new ExtractDatePartRenderStrategy();
        sqlRenderer = SqlRendererFactory.standardSql2008();
    }

    @Test
    void year_columnReference() {
        ExtractDatePart func = ExtractDatePart.year(ColumnReference.of("Customer", "birthdate"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("YEAR(\"Customer\".\"birthdate\")");
    }

    @Test
    void month_columnReference() {
        ExtractDatePart func = ExtractDatePart.month(ColumnReference.of("Customer", "birthdate"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("MONTH(\"Customer\".\"birthdate\")");
    }

    @Test
    void day_columnReference() {
        ExtractDatePart func = ExtractDatePart.day(ColumnReference.of("Customer", "birthdate"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("DAY(\"Customer\".\"birthdate\")");
    }

    @Test
    void year_literal() {
        ExtractDatePart func = ExtractDatePart.year(Literal.of("2023-01-15"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("YEAR('2023-01-15')");
    }

    @Test
    void month_literal() {
        ExtractDatePart func = ExtractDatePart.month(Literal.of("2023-01-15"));
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("MONTH('2023-01-15')");
    }

    @Test
    void year_expression() {
        ScalarExpression currentTs = new CurrentDateTime();
        ExtractDatePart func = ExtractDatePart.year(currentTs);
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("YEAR(CURRENT_TIMESTAMP())");
    }

    @Test
    void day_expression() {
        ScalarExpression currentTs = new CurrentDateTime();
        ExtractDatePart func = ExtractDatePart.day(currentTs);
        String sql = strategy.render(func, sqlRenderer);
        assertThat(sql).isEqualTo("DAY(CURRENT_TIMESTAMP())");
    }
}
