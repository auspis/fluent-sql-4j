package lan.tlab.r4j.sql.ast.common.expression.scalar.function.json;

import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the JSON_EXISTS function from SQL:2016 standard.
 * JSON_EXISTS checks whether a JSON path expression returns any data.
 *
 * <p>Syntax: JSON_EXISTS(json_doc, path [ON ERROR behavior])
 *
 * <p>Example usage:
 * <pre>{@code
 * // Basic usage with defaults
 * JsonExists jsonExists = new JsonExists(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.price")
 * );
 *
 * // With error behavior
 * JsonExists withError = new JsonExists(
 *     ColumnReference.of("products", "data"),
 *     Literal.of("$.price"),
 *     BehaviorKind.ERROR
 * );
 * }</pre>
 */
public record JsonExists(ScalarExpression jsonDocument, ScalarExpression path, BehaviorKind onErrorBehavior)
        implements FunctionCall {

    /**
     * Compact constructor that sets default values for null parameters.
     * The default error behavior returns NULL.
     */
    public JsonExists {
        if (onErrorBehavior == null) {
            onErrorBehavior = BehaviorKind.NONE;
        }
    }

    /**
     * Creates a JsonExists with default behavior (NULL on error).
     *
     * @param jsonDocument the JSON document expression
     * @param path the JSON path expression
     */
    public JsonExists(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
