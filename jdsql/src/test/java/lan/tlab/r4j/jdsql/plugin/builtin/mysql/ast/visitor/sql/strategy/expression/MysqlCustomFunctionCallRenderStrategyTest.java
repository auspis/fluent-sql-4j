package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression.MysqlCustomFunctionCallRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MysqlCustomFunctionCallRenderStrategyTest {

    private MysqlCustomFunctionCallRenderStrategy strategy;
    private SqlRenderer sqlRenderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new MysqlCustomFunctionCallRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.mysql();
        ctx = new AstContext();
    }

    @Test
    void renderFunctionWithoutArguments() {
        CustomFunctionCall functionCall = new CustomFunctionCall("MY_FUNCTION", List.of(), Map.of());

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("MY_FUNCTION()");
    }

    @Test
    void renderFunctionWithSingleArgument() {
        CustomFunctionCall functionCall = new CustomFunctionCall("UPPER", List.of(Literal.of("hello")), Map.of());

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("UPPER('hello')");
    }

    @Test
    void renderFunctionWithMultipleArguments() {
        CustomFunctionCall functionCall = new CustomFunctionCall(
                "CONCAT", List.of(Literal.of("hello"), Literal.of(" "), Literal.of("world")), Map.of());

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("CONCAT('hello', ' ', 'world')");
    }

    @Test
    void renderGroupConcatWithSeparator() {
        CustomFunctionCall functionCall =
                new CustomFunctionCall("GROUP_CONCAT", List.of(Literal.of("name")), Map.of("SEPARATOR", ", "));

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("GROUP_CONCAT('name' SEPARATOR ', ')");
    }

    @Test
    void renderGroupConcatWithOrderByAndSeparator() {
        CustomFunctionCall functionCall = new CustomFunctionCall(
                "GROUP_CONCAT",
                List.of(Literal.of("name")),
                Map.of(
                        "ORDER BY", "name ASC",
                        "SEPARATOR", "|"));

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("GROUP_CONCAT('name' ORDER BY 'name ASC' SEPARATOR '|')");
    }

    @Test
    void renderFunctionWithMultipleOptions() {
        CustomFunctionCall functionCall = new CustomFunctionCall(
                "CUSTOM_FUNC",
                List.of(Literal.of("arg1"), Literal.of("arg2")),
                Map.of("OPTION1", "value1", "OPTION2", 42, "OPTION3", true));

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        // Options order is not guaranteed due to Map iteration
        assertThat(result).startsWith("CUSTOM_FUNC('arg1', 'arg2'");
        assertThat(result).contains("OPTION1 'value1'");
        assertThat(result).contains("OPTION2 42");
        assertThat(result).contains("OPTION3 true");
        assertThat(result).endsWith(")");
    }

    @Test
    void renderFunctionWithNonStringOptionValue() {
        CustomFunctionCall functionCall =
                new CustomFunctionCall("TEST_FUNC", List.of(Literal.of("test")), Map.of("NUMERIC_OPTION", 123));

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("TEST_FUNC('test' NUMERIC_OPTION 123)");
    }

    @Test
    void renderFunctionWithBooleanOptionValue() {
        CustomFunctionCall functionCall =
                new CustomFunctionCall("TEST_FUNC", List.of(Literal.of("test")), Map.of("BOOLEAN_OPTION", true));

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("TEST_FUNC('test' BOOLEAN_OPTION true)");
    }
}
