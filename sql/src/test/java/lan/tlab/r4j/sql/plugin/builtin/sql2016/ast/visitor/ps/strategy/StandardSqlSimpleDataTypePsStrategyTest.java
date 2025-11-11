package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.r4j.sql.ast.ddl.definition.DataType.SimpleDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlSimpleDataTypePsStrategyTest {

    private final SimpleDataTypePsStrategy strategy = new StandardSqlSimpleDataTypePsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void integerType() {
        SimpleDataType dataType = new SimpleDataType("INTEGER");

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("INTEGER");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void varcharType() {
        SimpleDataType dataType = new SimpleDataType("VARCHAR");

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("VARCHAR");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void textType() {
        SimpleDataType dataType = new SimpleDataType("TEXT");

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("TEXT");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void dateType() {
        SimpleDataType dataType = new SimpleDataType("DATE");

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("DATE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void timestampType() {
        SimpleDataType dataType = new SimpleDataType("TIMESTAMP");

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("TIMESTAMP");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void customType() {
        SimpleDataType dataType = new SimpleDataType("CUSTOM_TYPE");

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("CUSTOM_TYPE");
        assertThat(result.parameters()).isEmpty();
    }
}
