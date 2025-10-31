package lan.tlab.r4j.sql.ast.expression.scalar.call.function.json;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the JSON_QUERY function from SQL:2016 standard.
 * JSON_QUERY extracts a JSON object or array from a JSON document.
 *
 * <p>Syntax: JSON_QUERY(json_doc, path [RETURNING data_type] [wrapper_behavior] [ON EMPTY behavior] [ON ERROR behavior])
 *
 * <p>Example usage:
 * <pre>{@code
 * // Basic usage with defaults
 * JsonQuery jsonQuery = JsonQuery.of(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.items")
 * );
 *
 * // With wrapper behavior
 * JsonQuery withWrapper = JsonQuery.of(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.items"),
 *     WrapperBehavior.WITH_WRAPPER
 * );
 *
 * // With all options
 * JsonQuery complete = new JsonQuery(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.items"),
 *     "JSON",
 *     WrapperBehavior.WITH_WRAPPER,
 *     BehaviorKind.NULL,
 *     null,
 *     BehaviorKind.ERROR
 * );
 * }</pre>
 */
public record JsonQuery(
        ScalarExpression jsonDocument,
        ScalarExpression path,
        String returningType,
        WrapperBehavior wrapperBehavior,
        BehaviorKind onEmptyBehavior,
        String onEmptyDefault,
        BehaviorKind onErrorBehavior)
        implements FunctionCall {

    /**
     * Creates a JsonQuery with default behaviors (no wrapper, NULL on empty and error).
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     */
    public JsonQuery(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null, null, null, null, null);
    }

    /**
     * Factory method to create a JsonQuery with default behaviors.
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     * @return a new JsonQuery instance
     */
    public static JsonQuery of(ScalarExpression jsonDocument, ScalarExpression path) {
        return new JsonQuery(jsonDocument, path);
    }

    /**
     * Factory method to create a JsonQuery with wrapper behavior.
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     * @param wrapperBehavior the wrapper behavior to apply
     * @return a new JsonQuery instance
     */
    public static JsonQuery of(ScalarExpression jsonDocument, ScalarExpression path, WrapperBehavior wrapperBehavior) {
        return new JsonQuery(jsonDocument, path, null, wrapperBehavior, null, null, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
