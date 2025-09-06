package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.dialect.sqlserver.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.scalar.ColumnReference;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRendererImpl;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.CastRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlServerCastRenderStrategyTest {

    private CastRenderStrategy strategy;
    private SqlRendererImpl sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = CastRenderStrategy.sqlServer();
        sqlRenderer = SqlRendererFactory.sqlServer();
    }

    @Test
    void columnToVarchar() {
        Cast cast = Cast.of(ColumnReference.of("Customer", "id"), "VARCHAR(255)");
        String sql = strategy.render(cast, sqlRenderer);
        assertThat(sql).isEqualTo("CONVERT(VARCHAR(255), [Customer].[id])");
    }

    @Test
    void projection_castVarcharToInt() {
        Cast cast = Cast.of(Literal.of("12345"), "INT");
        String sql = strategy.render(cast, sqlRenderer);
        assertThat(sql).isEqualTo("CONVERT(INT, '12345')");
    }

    @Test
    void projection_castColumnToDatetime() {
        Cast cast = Cast.of(ColumnReference.of("my_table", "event_timestamp"), "DATETIME");
        String sql = strategy.render(cast, sqlRenderer);
        assertThat(sql).isEqualTo("CONVERT(DATETIME, [my_table].[event_timestamp])");
    }
}
