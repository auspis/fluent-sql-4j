package lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.dialect.sql2016;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ColumnReference;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.sql.ast.visitor.ps.PsDto;
import lan.tlab.r4j.sql.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.ps.strategy.StandardSqlCustomFunctionCallPsStrategy;
import org.junit.jupiter.api.Test;

class StandardSqlCustomFunctionCallPsStrategyTest {
    private final CustomFunctionCallPsStrategy strategy = new StandardSqlCustomFunctionCallPsStrategy();
    private final PreparedStatementRenderer renderer = new PreparedStatementRenderer();

    @Test
    void noArguments() {
        CustomFunctionCall function = new CustomFunctionCall("MY_FUNC", List.of(), Map.of());
        PsDto dto = strategy.handle(function, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("MY_FUNC()");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void singleArgument() {
        CustomFunctionCall function =
                new CustomFunctionCall("UPPER", List.of(ColumnReference.of("", "name")), Map.of());
        PsDto dto = strategy.handle(function, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("UPPER(\"name\")");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void multipleArguments() {
        CustomFunctionCall function = new CustomFunctionCall(
                "CONCAT",
                List.of(ColumnReference.of("", "first_name"), Literal.of(" "), ColumnReference.of("", "last_name")),
                Map.of());
        PsDto dto = strategy.handle(function, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("CONCAT(\"first_name\", ?, \"last_name\")");
        assertThat(dto.parameters()).containsExactly(" ");
    }

    @Test
    void withParameters() {
        CustomFunctionCall function = new CustomFunctionCall(
                "SUBSTR", List.of(ColumnReference.of("", "text"), Literal.of(1), Literal.of(10)), Map.of());
        PsDto dto = strategy.handle(function, renderer, new AstContext());
        assertThat(dto.sql()).isEqualTo("SUBSTR(\"text\", ?, ?)");
        assertThat(dto.parameters()).containsExactly(1, 10);
    }
}
