package lan.tlab.r4j.sql.ast.expression.scalar.call.function.number;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Mod implements FunctionCall {
    private final ScalarExpression dividend;
    private final ScalarExpression divisor;

    public static Mod of(Number dividend, Number divisor) {
        return of(Literal.of(dividend), Literal.of(divisor));
    }

    public static Mod of(ScalarExpression dividend, Number divisor) {
        return of(dividend, Literal.of(divisor));
    }

    public static Mod of(Number dividend, ScalarExpression divisor) {
        return of(Literal.of(dividend), divisor);
    }

    public static Mod of(ScalarExpression dividend, ScalarExpression divisor) {
        return new Mod(dividend, divisor);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
