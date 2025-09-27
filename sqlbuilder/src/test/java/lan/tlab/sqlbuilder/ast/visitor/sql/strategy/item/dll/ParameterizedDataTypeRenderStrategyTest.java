package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.item.dll;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.ParameterizedDataType;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.sql.factory.SqlRendererFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParameterizedDataTypeRenderStrategyTest {

    private SqlRenderer renderer;
    private ParameterizedDataTypeRenderStrategy strategy;

    @BeforeEach
    void setUp() {
        renderer = SqlRendererFactory.standardSql2008();
        strategy = new ParameterizedDataTypeRenderStrategy();
    }

    @Test
    void varchar() {
        ParameterizedDataType dataType = DataType.varchar(255);
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("VARCHAR(255)");

        dataType = new ParameterizedDataType("CHARACTER VARYING", List.of(Literal.of(255)));
        sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("CHARACTER VARYING(255)");
    }

    @Test
    void character() {
        ParameterizedDataType dataType = new ParameterizedDataType("CHARACTER", List.of(Literal.of(5)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("CHARACTER(5)");

        dataType = new ParameterizedDataType("CHAR", List.of(Literal.of(5)));
        sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("CHAR(5)");
    }

    @Test
    void decimal() {
        ParameterizedDataType dataType = new ParameterizedDataType("DECIMAL", List.of(Literal.of(10), Literal.of(2)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("DECIMAL(10, 2)");
    }

    @Test
    void numeric() {
        ParameterizedDataType dataType = new ParameterizedDataType("NUMERIC", List.of(Literal.of(5), Literal.of(2)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("NUMERIC(5, 2)");
    }

    @Test
    void binary() {
        ParameterizedDataType dataType = new ParameterizedDataType("BINARY", List.of(Literal.of(16)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("BINARY(16)");
    }

    @Test
    void varbinary() {
        ParameterizedDataType dataType = new ParameterizedDataType("VARBINARY", List.of(Literal.of(128)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("VARBINARY(128)");
    }

    @Test
    void interval() {
        ParameterizedDataType dataType = new ParameterizedDataType("INTERVAL", List.of(Literal.of(3)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("INTERVAL(3)");
    }

    @Test
    void timeWithTimeZone() {
        ParameterizedDataType dataType = new ParameterizedDataType("TIME WITH TIME ZONE", List.of(Literal.of(3)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("TIME WITH TIME ZONE(3)");
    }

    @Test
    void timestampWithTimeZone() {
        ParameterizedDataType dataType = new ParameterizedDataType("TIMESTAMP WITH TIME ZONE", List.of(Literal.of(3)));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("TIMESTAMP WITH TIME ZONE(3)");
    }

    @Test
    void array() {
        ParameterizedDataType varcharType = new ParameterizedDataType("VARCHAR", List.of(Literal.of(50)));
        ParameterizedDataType arrayType = new ParameterizedDataType("ARRAY", List.of(varcharType, Literal.of(10)));

        String sql = strategy.render(arrayType, renderer, new AstContext());
        assertThat(sql).isEqualTo("ARRAY(VARCHAR(50), 10)");
    }

    @Test
    void multiset() {
        SimpleDataType integerType = new SimpleDataType("INTEGER");
        ParameterizedDataType dataType = new ParameterizedDataType("MULTISET", List.of(integerType));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("MULTISET(INTEGER)");
    }

    @Test
    void row() {
        ParameterizedDataType varchar100Type = new ParameterizedDataType("VARCHAR", List.of(Literal.of(100)));
        ParameterizedDataType varchar50Type = new ParameterizedDataType("VARCHAR", List.of(Literal.of(50)));
        ParameterizedDataType dataType = new ParameterizedDataType("ROW", List.of(varchar100Type, varchar50Type));
        String sql = strategy.render(dataType, renderer, new AstContext());
        assertThat(sql).isEqualTo("ROW(VARCHAR(100), VARCHAR(50))");
    }
}
