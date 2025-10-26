package lan.tlab.r4j.sql.ast.expression.scalar.call.function.string;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Replace(ScalarExpression expression, ScalarExpression oldSubstring, ScalarExpression newSubstring)
        implements FunctionCall {

    public static Replace of(
            ScalarExpression expression, ScalarExpression oldSubstring, ScalarExpression newSubstring) {
        return new Replace(expression, oldSubstring, newSubstring);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
