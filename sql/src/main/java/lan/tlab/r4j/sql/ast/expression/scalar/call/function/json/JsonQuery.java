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
 * JsonQuery jsonQuery = new JsonQuery(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.items")
 * );
 *
 * // With wrapper behavior
 * JsonQuery withWrapper = new JsonQuery(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.items"),
 *     null,
 *     WrapperBehavior.WITH_WRAPPER
 * );
 *
 * // With all options
 * JsonQuery complete = new JsonQuery(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.items"),
 *     "JSON",
 *     WrapperBehavior.WITH_WRAPPER,
 *     OnEmptyBehavior.defaultValue("EMPTY ARRAY"),
 *     OnErrorBehavior.error()
 * );
 * }</pre>
 */
public record JsonQuery(
        ScalarExpression jsonDocument,
        ScalarExpression path,
        String returningType,
        WrapperBehavior wrapperBehavior,
        OnEmptyBehavior onEmptyBehavior,
        OnErrorBehavior onErrorBehavior)
        implements FunctionCall {

    /**
     * Compact constructor that sets default values for null parameters.
     * Default wrapper behavior is NONE, and default behaviors for empty/error return NULL.
     */
    public JsonQuery {
        if (wrapperBehavior == null) {
            wrapperBehavior = WrapperBehavior.NONE;
        }
        if (onEmptyBehavior == null) {
            onEmptyBehavior = OnEmptyBehavior.returnNull();
        }
        if (onErrorBehavior == null) {
            onErrorBehavior = OnErrorBehavior.returnNull();
        }
    }

    /**
     * Creates a JsonQuery with default behaviors (no wrapper, NULL on empty and error).
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     */
    public JsonQuery(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null, null, null, null);
    }

    /**
     * Creates a JsonQuery with wrapper behavior and other defaults.
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     * @param returningType the data type to return
     * @param wrapperBehavior the wrapper behavior to apply
     */
    public JsonQuery(
            ScalarExpression jsonDocument,
            ScalarExpression path,
            String returningType,
            WrapperBehavior wrapperBehavior) {
        this(jsonDocument, path, returningType, wrapperBehavior, null, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
