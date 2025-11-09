package lan.tlab.r4j.sql.ast.common.expression.scalar.function;

import java.util.List;
import java.util.Map;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents a custom/dialect-specific SQL function call.
 * <p>
 * This node is used for functions that are not part of standard SQL
 * and are specific to a particular database dialect (e.g., MySQL's GROUP_CONCAT,
 * PostgreSQL's STRING_AGG, etc.).
 * <p>
 * The function is represented by:
 * <ul>
 *   <li><b>functionName</b>: The name of the function (e.g., "GROUP_CONCAT")</li>
 *   <li><b>arguments</b>: List of scalar expressions as function arguments</li>
 *   <li><b>options</b>: Additional options/modifiers (e.g., ORDER BY, SEPARATOR, DISTINCT)</li>
 * </ul>
 *
 * @param functionName the name of the custom function
 * @param arguments the list of arguments to the function
 * @param options additional options/modifiers as key-value pairs
 */
public record CustomFunctionCall(String functionName, List<ScalarExpression> arguments, Map<String, Object> options)
        implements ScalarExpression {

    public CustomFunctionCall {
        if (functionName == null || functionName.trim().isEmpty()) {
            throw new IllegalArgumentException("Function name cannot be null or empty");
        }
        arguments = arguments == null ? List.of() : List.copyOf(arguments);
        options = options == null ? Map.of() : Map.copyOf(options);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
