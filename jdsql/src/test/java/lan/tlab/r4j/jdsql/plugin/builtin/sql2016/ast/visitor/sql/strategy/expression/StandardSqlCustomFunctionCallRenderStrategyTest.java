package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import lan.tlab.r4j.jdsql.test.util.TestDialectRendererFactory;
import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.StandardSqlCustomFunctionCallRenderStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class StandardSqlCustomFunctionCallRenderStrategyTest {

    private StandardSqlCustomFunctionCallRenderStrategy strategy;
    private SqlRenderer sqlRenderer;
    private AstContext ctx;

    @BeforeEach
    void setUp() {
        strategy = new StandardSqlCustomFunctionCallRenderStrategy();
        sqlRenderer = TestDialectRendererFactory.standardSql();
        ctx = new AstContext();
    }

    @Test
    void renderFunctionWithoutArguments() {
        CustomFunctionCall functionCall = new CustomFunctionCall("CURRENT_TIMESTAMP", List.of(), Map.of());

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("CURRENT_TIMESTAMP()");
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
    void renderFunctionIgnoresOptions() {
        // Standard SQL strategy ignores options (unlike MySQL which handles SEPARATOR, etc.)
        CustomFunctionCall functionCall = new CustomFunctionCall(
                "CUSTOM_FUNC", List.of(Literal.of("arg1")), Map.of("SEPARATOR", ", ", "ORDER BY", "name"));

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        // Options are completely ignored - no SEPARATOR or ORDER BY in output
        assertThat(result).isEqualTo("CUSTOM_FUNC('arg1')");
    }

    @Test
    void renderFunctionWithComplexName() {
        CustomFunctionCall functionCall = new CustomFunctionCall(
                "JSON_EXTRACT_PATH",
                List.of(Literal.of("json_column"), Literal.of("field1"), Literal.of("field2")),
                Map.of());

        String result = strategy.render(functionCall, sqlRenderer, ctx);

        assertThat(result).isEqualTo("JSON_EXTRACT_PATH('json_column', 'field1', 'field2')");
    }
}
