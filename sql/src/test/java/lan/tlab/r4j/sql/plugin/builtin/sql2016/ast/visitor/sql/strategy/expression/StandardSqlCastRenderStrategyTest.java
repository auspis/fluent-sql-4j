package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CastRenderStrategy;
import lan.tlab.r4j.sql.test.TestDialectRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCastRenderStrategyTest {

    private CastRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = new StandardSqlCastRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
    }

    @Test
    void columnToVarchar() {
        Cast cast = Cast.of(ColumnReference.of("Customer", "id"), "VARCHAR(255)");
        String sql = strategy.render(cast, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CAST(\"Customer\".\"id\" AS VARCHAR(255))");
    }

    @Test
    void intToDouble() {
        Cast cast = Cast.of(Literal.of(123), "DECIMAL(10,2)");
        String sql = strategy.render(cast, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CAST(123 AS DECIMAL(10,2))");
    }

    @Test
    void tringToDate() {
        Cast cast = Cast.of(Literal.of("2023-08-01"), "DATE");
        String sql = strategy.render(cast, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CAST('2023-08-01' AS DATE)");
    }

    @Test
    void projection_castArithmeticExpressionToBigInt() {
        Cast cast = Cast.of(
                ArithmeticExpression.addition(
                        ColumnReference.of("my_table", "value1"), ColumnReference.of("my_table", "value2")),
                "BIGINT");
        String sql = strategy.render(cast, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CAST((\"my_table\".\"value1\" + \"my_table\".\"value2\") AS BIGINT)");
    }
}
