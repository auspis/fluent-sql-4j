package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class SimpleDataTypeRenderStrategyTest {

    private SimpleDataTypeRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new SimpleDataTypeRenderStrategy();
        renderer = SqlRendererFactory.standardSql2008();
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "INTEGER",
                "SMALLINT",
                "BIGINT",
                "FLOAT",
                "REAL",
                "DOUBLE PRECISION",
                "DATE",
                "TIME",
                "TIMESTAMP",
                "BOOLEAN",
                "XML",
                "UDT"
            })
    void ok(String typeName) {
        SimpleDataType dataType = new SimpleDataType(typeName);
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo(typeName);
    }
}
