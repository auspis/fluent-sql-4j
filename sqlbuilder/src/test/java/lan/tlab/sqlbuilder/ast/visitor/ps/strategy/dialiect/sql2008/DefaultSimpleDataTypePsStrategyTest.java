package lan.tlab.sqlbuilder.ast.visitor.ps.strategy.dialiect.sql2008;

import static org.assertj.core.api.Assertions.assertThat;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.DataType.SimpleDataType;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.ps.PreparedStatementVisitor;
import lan.tlab.sqlbuilder.ast.visitor.ps.PsDto;
import lan.tlab.sqlbuilder.ast.visitor.ps.strategy.SimpleDataTypePsStrategy;
import org.junit.jupiter.api.Test;

class DefaultSimpleDataTypePsStrategyTest {

    private final SimpleDataTypePsStrategy strategy = new DefaultSimpleDataTypePsStrategy();
    private final PreparedStatementVisitor visitor = new PreparedStatementVisitor();
    private final AstContext ctx = new AstContext();

    @Test
    void integerType() {
        SimpleDataType dataType = new SimpleDataType("INTEGER");

        PsDto result = strategy.handle(dataType, visitor, ctx);

        assertThat(result.sql()).isEqualTo("INTEGER");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void varcharType() {
        SimpleDataType dataType = new SimpleDataType("VARCHAR");

        PsDto result = strategy.handle(dataType, visitor, ctx);

        assertThat(result.sql()).isEqualTo("VARCHAR");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void textType() {
        SimpleDataType dataType = new SimpleDataType("TEXT");

        PsDto result = strategy.handle(dataType, visitor, ctx);

        assertThat(result.sql()).isEqualTo("TEXT");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void dateType() {
        SimpleDataType dataType = new SimpleDataType("DATE");

        PsDto result = strategy.handle(dataType, visitor, ctx);

        assertThat(result.sql()).isEqualTo("DATE");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void timestampType() {
        SimpleDataType dataType = new SimpleDataType("TIMESTAMP");

        PsDto result = strategy.handle(dataType, visitor, ctx);

        assertThat(result.sql()).isEqualTo("TIMESTAMP");
        assertThat(result.parameters()).isEmpty();
    }

    @Test
    void customType() {
        SimpleDataType dataType = new SimpleDataType("CUSTOM_TYPE");

        PsDto result = strategy.handle(dataType, visitor, ctx);

        assertThat(result.sql()).isEqualTo("CUSTOM_TYPE");
        assertThat(result.parameters()).isEmpty();
    }
}
