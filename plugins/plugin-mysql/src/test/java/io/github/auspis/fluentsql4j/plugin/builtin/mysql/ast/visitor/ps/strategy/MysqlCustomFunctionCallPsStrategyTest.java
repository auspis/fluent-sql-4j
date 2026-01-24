package io.github.auspis.fluentsql4j.plugin.builtin.mysql.ast.visitor.ps.strategy;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.auspis.fluentsql4j.ast.core.expression.function.CustomFunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ColumnReference;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.Literal;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.ps.AstToPreparedStatementSpecVisitor;
import io.github.auspis.fluentsql4j.ast.visitor.ps.PreparedStatementSpec;
import io.github.auspis.fluentsql4j.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class MysqlCustomFunctionCallPsStrategyTest {
    private final CustomFunctionCallPsStrategy strategy = new MysqlCustomFunctionCallPsStrategy();
    private final AstToPreparedStatementSpecVisitor specFactory = new AstToPreparedStatementSpecVisitor();

    @Test
    void noArgumentsNoOptions() {
        CustomFunctionCall function = new CustomFunctionCall("MY_FUNC", List.of(), Map.of());
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("MY_FUNC()");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void singleArgumentNoOptions() {
        CustomFunctionCall function =
                new CustomFunctionCall("UPPER", List.of(ColumnReference.of("", "name")), Map.of());
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("UPPER(\"name\")");
        assertThat(dto.parameters()).isEmpty();
    }

    @Test
    void groupConcatWithSeparator() {
        CustomFunctionCall function = new CustomFunctionCall(
                "GROUP_CONCAT", List.of(ColumnReference.of("", "name")), Map.of("SEPARATOR", ", "));
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("GROUP_CONCAT(\"name\" SEPARATOR ?)");
        assertThat(dto.parameters()).containsExactly(", ");
    }

    @Test
    void groupConcatWithParametersAndSeparator() {
        CustomFunctionCall function = new CustomFunctionCall(
                "GROUP_CONCAT",
                List.of(ColumnReference.of("", "name"), Literal.of("ORDER BY"), ColumnReference.of("", "id")),
                Map.of("SEPARATOR", ";"));
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        assertThat(dto.sql()).isEqualTo("GROUP_CONCAT(\"name\", ?, \"id\" SEPARATOR ?)");
        assertThat(dto.parameters()).containsExactly("ORDER BY", ";");
    }

    @Test
    void multipleOptions() {
        CustomFunctionCall function = new CustomFunctionCall(
                "CUSTOM_FUNC", List.of(ColumnReference.of("", "col1")), Map.of("OPTION1", "value1", "OPTION2", 42));
        PreparedStatementSpec dto = strategy.handle(function, specFactory, new AstContext());
        // Note: Map iteration order is not guaranteed, so we check that both options are present with ? placeholders
        String sql = dto.sql();
        assertThat(sql).startsWith("CUSTOM_FUNC(\"col1\" ");
        assertThat(sql).contains("OPTION1 ?");
        assertThat(sql).contains("OPTION2 ?");
        assertThat(sql).endsWith(")");
        assertThat(dto.parameters()).containsExactlyInAnyOrder("value1", 42);
    }
}
