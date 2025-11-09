package lan.tlab.r4j.sql.plugin.builtin.mysql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.CustomFunctionCallRenderStrategy;

/**
 * Render strategy for MySQL custom function calls.
 * <p>
 * This strategy handles rendering of custom functions with options, such as:
 * <ul>
 *   <li>GROUP_CONCAT with SEPARATOR option</li>
 * </ul>
 * <p>
 * Example:
 * <pre>
 * GROUP_CONCAT(name SEPARATOR ', ')
 * </pre>
 */
public class MysqlCustomFunctionCallRenderStrategy implements CustomFunctionCallRenderStrategy {

    @Override
    public String render(CustomFunctionCall functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        String args = functionCall.arguments().stream()
                .map(arg -> arg.accept(sqlRenderer, ctx))
                .collect(Collectors.joining(", "));

        StringBuilder result = new StringBuilder();
        result.append(functionCall.functionName()).append("(").append(args);

        // Handle function options (e.g., SEPARATOR for GROUP_CONCAT)
        if (!functionCall.options().isEmpty()) {
            functionCall.options().forEach((key, value) -> {
                result.append(" ").append(key).append(" ");
                if (value instanceof String) {
                    // Escape string values with single quotes
                    result.append("'").append(value).append("'");
                } else {
                    result.append(value);
                }
            });
        }

        result.append(")");
        return result.toString();
    }
}
