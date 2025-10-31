package lan.tlab.r4j.sql.ast.expression.scalar.call.function.json;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the JSON_VALUE function from SQL:2016 standard.
 * JSON_VALUE extracts a scalar value from a JSON document.
 *
 * <p>Syntax: JSON_VALUE(json_doc, path [RETURNING data_type] [ON EMPTY behavior] [ON ERROR behavior])
 *
 * <p>Example usage:
 * <pre>{@code
 * // Basic usage with defaults
 * JsonValue jsonValue = JsonValue.of(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.price")
 * );
 *
 * // With returning type
 * JsonValue withType = JsonValue.of(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.price"),
 *     "DECIMAL(10,2)"
 * );
 *
 * // With all options
 * JsonValue complete = new JsonValue(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.price"),
 *     "DECIMAL(10,2)",
 *     BehaviorKind.DEFAULT,
 *     "0.0",
 *     BehaviorKind.NULL
 * );
 * }</pre>
 */
public record JsonValue(
        ScalarExpression jsonDocument,
        ScalarExpression path,
        String returningType,
        BehaviorKind onEmptyBehavior,
        String onEmptyDefault,
        BehaviorKind onErrorBehavior)
        implements FunctionCall {

    /**
     * Creates a JsonValue with default behaviors (NULL on empty and error).
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     */
    public JsonValue(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null, null, null, null);
    }

    /**
     * Factory method to create a JsonValue with default behaviors.
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     * @return a new JsonValue instance
     */
    public static JsonValue of(ScalarExpression jsonDocument, ScalarExpression path) {
        return new JsonValue(jsonDocument, path);
    }

    /**
     * Factory method to create a JsonValue with a returning type.
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     * @param returningType the data type to return
     * @return a new JsonValue instance
     */
    public static JsonValue of(ScalarExpression jsonDocument, ScalarExpression path, String returningType) {
        return new JsonValue(jsonDocument, path, returningType, null, null, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
