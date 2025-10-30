package lan.tlab.r4j.sql.ast.expression.scalar.call.function.json;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the JSON_QUERY function from SQL:2016 standard.
 * JSON_QUERY extracts a JSON object or array from a JSON document.
 *
 * Syntax: JSON_QUERY(json_doc, path [RETURNING data_type] [wrapper_behavior] [ON EMPTY behavior] [ON ERROR behavior])
 */
public record JsonQuery(
        ScalarExpression jsonDocument,
        ScalarExpression path,
        String returningType,
        String wrapperBehavior,
        String onEmptyBehavior,
        String onErrorBehavior)
        implements FunctionCall {

    public JsonQuery(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null, null, null, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
