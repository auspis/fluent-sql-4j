package lan.tlab.r4j.sql.ast.expression.scalar.call.function.json;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

/**
 * Represents the JSON_EXISTS function from SQL:2016 standard.
 * JSON_EXISTS checks whether a JSON path expression returns any data.
 *
 * Syntax: JSON_EXISTS(json_doc, path [ON ERROR behavior])
 */
public record JsonExists(ScalarExpression jsonDocument, ScalarExpression path, String onErrorBehavior)
        implements FunctionCall {

    public JsonExists(ScalarExpression jsonDocument, ScalarExpression path) {
        this(jsonDocument, path, null);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
