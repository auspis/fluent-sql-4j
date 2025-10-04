package lan.tlab.r4j.sql.ast.expression.scalar.call.function.string;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Replace implements FunctionCall {

    private final ScalarExpression expression;
    private final ScalarExpression oldSubstring;
    private final ScalarExpression newSubstring;

    public static Replace of(
            ScalarExpression expression, ScalarExpression oldSubstring, ScalarExpression newSubstring) {
        return new Replace(expression, oldSubstring, newSubstring);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
