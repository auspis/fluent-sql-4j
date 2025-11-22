package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.jdsql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.plugin.builtin.sql2016.StandardSqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class StandardSqlSimpleDataTypeRenderStrategyTest {

    private StandardSqlSimpleDataTypeRenderStrategy strategy;
    private SqlRenderer renderer;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlSimpleDataTypeRenderStrategy();
        renderer = StandardSqlRendererFactory.standardSql();
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
