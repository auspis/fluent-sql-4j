package lan.tlab.r4j.sql.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.statement.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.factory.SqlRendererFactory;
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
