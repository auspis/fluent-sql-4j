package lan.tlab.sqlbuilder.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression.DataLengthRenderStrategy;
import org.junit.jupiter.api.Test;

class MySqlRenderStrategiesTest {
    private final SqlRenderer sqlRenderer = SqlRendererFactory.mysql();

    @Test
    void testDataLengthRenderStrategyMysql() {
        DataLength dataLength = new DataLength(Literal.of("abc"));
        String sql = DataLengthRenderStrategy.mysql().render(dataLength, sqlRenderer);
        assertEquals("LENGTH(\"abc\")", sql);
    }
}
