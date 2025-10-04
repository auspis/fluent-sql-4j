package lan.tlab.r4j.sql.ast.visitor.sql.dialect.sqlserver.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CastRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SqlServerCastRenderStrategyTest {

    private CastRenderStrategy strategy;
    private SqlRenderer sqlRenderer;

    @BeforeEach
    public void setUp() {
        strategy = CastRenderStrategy.sqlServer();
        sqlRenderer = SqlRendererFactory.sqlServer();
    }

    @Test
    void columnToVarchar() {
        Cast cast = Cast.of(ColumnReference.of("Customer", "id"), "VARCHAR(255)");
        String sql = strategy.render(cast, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CONVERT(VARCHAR(255), [Customer].[id])");
    }

    @Test
    void projection_castVarcharToInt() {
        Cast cast = Cast.of(Literal.of("12345"), "INT");
        String sql = strategy.render(cast, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CONVERT(INT, '12345')");
    }

    @Test
    void projection_castColumnToDatetime() {
        Cast cast = Cast.of(ColumnReference.of("my_table", "event_timestamp"), "DATETIME");
        String sql = strategy.render(cast, sqlRenderer, new AstContext());
        assertThat(sql).isEqualTo("CONVERT(DATETIME, [my_table].[event_timestamp])");
    }
}
