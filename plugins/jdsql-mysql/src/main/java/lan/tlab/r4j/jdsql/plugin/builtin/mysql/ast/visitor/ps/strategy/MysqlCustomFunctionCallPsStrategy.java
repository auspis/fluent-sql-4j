package lan.tlab.r4j.jdsql.plugin.builtin.mysql.ast.visitor.ps.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.CustomFunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.ps.PreparedStatementSpec;
import lan.tlab.r4j.jdsql.ast.visitor.ps.strategy.CustomFunctionCallPsStrategy;

/**
 * MySQL strategy for rendering custom function calls in prepared statements.
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
public class MysqlCustomFunctionCallPsStrategy implements CustomFunctionCallPsStrategy {

    @Override
    public PreparedStatementSpec handle(
            CustomFunctionCall functionCall, PreparedStatementRenderer renderer, AstContext ctx) {
        List<Object> allParams = new ArrayList<>();

        String args = functionCall.arguments().stream()
                .map(arg -> {
                    PreparedStatementSpec argDto = arg.accept(renderer, ctx);
                    allParams.addAll(argDto.parameters());
                    return argDto.sql();
                })
                .collect(Collectors.joining(", "));

        StringBuilder result = new StringBuilder();
        result.append(functionCall.functionName()).append("(").append(args);

        // Handle function options (e.g., SEPARATOR for GROUP_CONCAT)
        if (!functionCall.options().isEmpty()) {
            // For MySQL, ORDER BY should come before SEPARATOR
            if (functionCall.options().containsKey("ORDER BY")) {
                result.append(" ORDER BY ");
                Object orderByValue = functionCall.options().get("ORDER BY");
                if (orderByValue instanceof String) {
                    result.append("'").append(orderByValue).append("'");
                } else {
                    result.append(orderByValue);
                }
            }
            if (functionCall.options().containsKey("SEPARATOR")) {
                result.append(" SEPARATOR ");
                Object separatorValue = functionCall.options().get("SEPARATOR");
                if (separatorValue instanceof String) {
                    result.append("'").append(separatorValue).append("'");
                } else {
                    result.append(separatorValue);
                }
            }
            // Handle any other options
            functionCall.options().forEach((key, value) -> {
                if (!"ORDER BY".equals(key) && !"SEPARATOR".equals(key)) {
                    result.append(" ").append(key).append(" ");
                    if (value instanceof String) {
                        result.append("'").append(value).append("'");
                    } else {
                        result.append(value);
                    }
                }
            });
        }

        result.append(")");
        return new PreparedStatementSpec(result.toString(), allParams);
    }
}
