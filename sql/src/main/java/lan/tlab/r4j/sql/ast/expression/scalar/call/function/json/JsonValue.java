package lan.tlab.r4j.sql.ast.expression.scalar.call.function.json;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the JSON_VALUE function from SQL:2016 standard.
 * JSON_VALUE extracts a scalar value from a JSON document.
 *
 * Syntax: JSON_VALUE(json_doc, path [RETURNING data_type] [ON EMPTY behavior] [ON ERROR behavior])
 */
public record JsonValue(
        ScalarExpression jsonDocument,
        ScalarExpression path,
        String returningType,
        String onEmptyBehavior,
        String onErrorBehavior)
        implements FunctionCall {

    public JsonValue(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null, null, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
