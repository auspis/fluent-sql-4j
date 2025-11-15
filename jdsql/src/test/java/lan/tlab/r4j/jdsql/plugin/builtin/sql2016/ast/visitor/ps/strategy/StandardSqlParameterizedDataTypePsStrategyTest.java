package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.ddl.definition.DataType.ParameterizedDataType;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.ParameterizedDataTypePsStrategy;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlParameterizedDataTypePsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlParameterizedDataTypePsStrategyTest {

    private final ParameterizedDataTypePsStrategy strategy = new StandardSqlParameterizedDataTypePsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();
    private final AstContext ctx = new AstContext();

    @Test
    void varcharWithLiteralLength() {
        ParameterizedDataType dataType = new ParameterizedDataType("VARCHAR", List.of(Literal.of(255)));

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("VARCHAR(?)");
        assertThat(result.parameters()).hasSize(1);
        assertThat(result.parameters().get(0)).isEqualTo(255);
    }

    @Test
    void decimalWithPrecisionAndScale() {
        ParameterizedDataType dataType = new ParameterizedDataType("DECIMAL", List.of(Literal.of(10), Literal.of(2)));

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("DECIMAL(?, ?)");
        assertThat(result.parameters()).hasSize(2);
        assertThat(result.parameters().get(0)).isEqualTo(10);
        assertThat(result.parameters().get(1)).isEqualTo(2);
    }

    @Test
    void charWithFixedLength() {
        ParameterizedDataType dataType = new ParameterizedDataType("CHAR", List.of(Literal.of(10)));

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("CHAR(?)");
        assertThat(result.parameters()).hasSize(1);
        assertThat(result.parameters().get(0)).isEqualTo(10);
    }

    @Test
    void numericWithPrecision() {
        ParameterizedDataType dataType = new ParameterizedDataType("NUMERIC", List.of(Literal.of(18)));

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("NUMERIC(?)");
        assertThat(result.parameters()).hasSize(1);
        assertThat(result.parameters().get(0)).isEqualTo(18);
    }

    @Test
    void customTypeWithMultipleParameters() {
        ParameterizedDataType dataType = new ParameterizedDataType(
                "CUSTOM_TYPE", List.of(Literal.of("param1"), Literal.of(42), Literal.of(true)));

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("CUSTOM_TYPE(?, ?, ?)");
        assertThat(result.parameters()).hasSize(3);
        assertThat(result.parameters().get(0)).isEqualTo("param1");
        assertThat(result.parameters().get(1)).isEqualTo(42);
        assertThat(result.parameters().get(2)).isEqualTo(true);
    }

    @Test
    void typeWithoutParameters() {
        ParameterizedDataType dataType = new ParameterizedDataType("SOME_TYPE", List.of());

        PsDto result = strategy.handle(dataType, renderer, ctx);

        assertThat(result.sql()).isEqualTo("SOME_TYPE()");
        assertThat(result.parameters()).isEmpty();
    }
}
