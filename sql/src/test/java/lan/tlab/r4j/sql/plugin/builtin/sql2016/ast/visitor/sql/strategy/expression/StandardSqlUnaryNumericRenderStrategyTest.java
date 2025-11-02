package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.UnaryNumeric;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlUnaryNumericRenderStrategyTest {

    private StandardSqlUnaryNumericRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlUnaryNumericRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql2008();
    }

    @Test
    void abs_columnReference() {
        UnaryNumeric func = UnaryNumeric.abs(ColumnReference.of("Customer", "value"));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ABS(\"Customer\".\"value\")");
    }

    @Test
    void abs_integer() {
        UnaryNumeric func = UnaryNumeric.abs(-10);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ABS(-10)");
    }

    @Test
    void abs_double() {
        UnaryNumeric func = UnaryNumeric.abs(Literal.of(-5.75));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ABS(-5.75)");
    }

    @Test
    void abs_arithmeticExpression() {
        UnaryNumeric func = UnaryNumeric.abs(ArithmeticExpression.subtraction(
                ColumnReference.of("Customer", "price"), ColumnReference.of("Customer", "cost")));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("ABS((\"Customer\".\"price\" - \"Customer\".\"cost\"))");
    }

    @Test
    void ceil_columnReference() {
        ScalarExpression valueCol = ColumnReference.of("my_table", "decimal_value");

        UnaryNumeric func = UnaryNumeric.ceil(valueCol);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CEIL(\"my_table\".\"decimal_value\")");
    }

    @Test
    void ceil_double() {
        UnaryNumeric func = UnaryNumeric.ceil(10.1);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CEIL(10.1)");
    }

    @Test
    void ceil_negativeDoubleLiteral() {
        UnaryNumeric func = UnaryNumeric.ceil(Literal.of(-5.7));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CEIL(-5.7)");
    }

    @Test
    void ceil_integer() {
        UnaryNumeric func = UnaryNumeric.ceil(15);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CEIL(15)");
    }

    @Test
    void ceil_arithmeticExpression() {
        UnaryNumeric func = UnaryNumeric.ceil(ArithmeticExpression.division(
                ColumnReference.of("my_table", "total_cost"), ColumnReference.of("my_table", "item_count")));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CEIL((\"my_table\".\"total_cost\" / \"my_table\".\"item_count\"))");
    }

    @Test
    void floor_columnReference() {
        ScalarExpression valueCol = ColumnReference.of("my_table", "decimal_value");

        UnaryNumeric func = UnaryNumeric.floor(valueCol);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FLOOR(\"my_table\".\"decimal_value\")");
    }

    @Test
    void floor_double() {
        UnaryNumeric func = UnaryNumeric.floor(10.1);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FLOOR(10.1)");
    }

    @Test
    void floor_negativeDoubleLiteral() {
        UnaryNumeric func = UnaryNumeric.floor(Literal.of(-5.7));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FLOOR(-5.7)");
    }

    @Test
    void floor_integer() {
        UnaryNumeric func = UnaryNumeric.floor(15);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FLOOR(15)");
    }

    @Test
    void floor_arithmeticExpression() {
        UnaryNumeric func = UnaryNumeric.floor(ArithmeticExpression.division(
                ColumnReference.of("my_table", "total_cost"), ColumnReference.of("my_table", "item_count")));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("FLOOR((\"my_table\".\"total_cost\" / \"my_table\".\"item_count\"))");
    }

    @Test
    void sqrt_columnReference() {
        ScalarExpression valueCol = ColumnReference.of("my_table", "decimal_value");

        UnaryNumeric func = UnaryNumeric.sqrt(valueCol);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SQRT(\"my_table\".\"decimal_value\")");
    }

    @Test
    void sqrt_double() {
        UnaryNumeric func = UnaryNumeric.sqrt(10.1);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SQRT(10.1)");
    }

    @Test
    void sqrt_negativeDoubleLiteral() {
        UnaryNumeric func = UnaryNumeric.sqrt(Literal.of(-5.7));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SQRT(-5.7)");
    }

    @Test
    void sqrt_integer() {
        UnaryNumeric func = UnaryNumeric.sqrt(15);
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SQRT(15)");
    }

    @Test
    void sqrt_arithmeticExpression() {
        UnaryNumeric func = UnaryNumeric.sqrt(ArithmeticExpression.division(
                ColumnReference.of("my_table", "total_cost"), ColumnReference.of("my_table", "item_count")));
        String sql = strategy.render(func, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("SQRT((\"my_table\".\"total_cost\" / \"my_table\".\"item_count\"))");
    }
}
