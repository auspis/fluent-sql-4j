package io.github.auspis.fluentsql4j.ast.core.expression.function.json;

import io.github.auspis.fluentsql4j.ast.core.expression.function.FunctionCall;
import io.github.auspis.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

/**
 * Represents the JSON_VALUE function from SQL:2016 standard.
 * JSON_VALUE extracts a scalar value from a JSON document.
 *
 * <p>Syntax: JSON_VALUE(json_doc, path [RETURNING data_type] [ON EMPTY behavior] [ON ERROR behavior])
 *
 * <p>Example usage:
 * <pre>{@code
 * // Basic usage with defaults
 * JsonValue jsonValue = new JsonValue(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.price")
 * );
 *
 * // With returning type
 * JsonValue withType = new JsonValue(
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
 *     OnEmptyBehavior.defaultValue("0.0"),
 *     BehaviorKind.NULL
 * );
 * }</pre>
 */
public record JsonValue(
        ScalarExpression jsonDocument,
        ScalarExpression path,
        String returningType,
        OnEmptyBehavior onEmptyBehavior,
        BehaviorKind onErrorBehavior)
        implements FunctionCall {

    /**
     * Compact constructor that sets default values for null parameters.
     * Default behaviors return NULL for both empty and error conditions.
     */
    public JsonValue {
        if (onEmptyBehavior == null) {
            onEmptyBehavior = OnEmptyBehavior.returnNull();
        }
        if (onErrorBehavior == null) {
            onErrorBehavior = BehaviorKind.NONE;
        }
    }

    /**
     * Creates a JsonValue with default behaviors (NULL on empty and error).
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     */
    public JsonValue(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null, null, null);
    }

    /**
     * Creates a JsonValue with a returning type and default behaviors.
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     * @param returningType the data type to return
     */
    public JsonValue(ScalarExpression jsonDocument, ScalarExpression path, String returningType) {
        this(jsonDocument, path, returningType, null, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
