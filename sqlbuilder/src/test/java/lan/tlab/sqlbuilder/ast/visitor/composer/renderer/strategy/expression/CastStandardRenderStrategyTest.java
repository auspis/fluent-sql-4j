package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ArithmeticExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CastStandardRenderStrategyTest {

    private CastRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = CastRenderStrategy.standard();
        sqlRenderer = SqlRendererFactory.standardSql2008();
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
