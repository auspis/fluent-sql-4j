package lan.tlab.r4j.jdsql.ast.core.expression.function.number;

import lan.tlab.r4j.jdsql.ast.core.expression.function.FunctionCall;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Mod(ScalarExpression dividend, ScalarExpression divisor) implements FunctionCall {

    public static Mod of(Number dividend, Number divisor) {
        return new Mod(Literal.of(dividend), Literal.of(divisor));
    }

    public static Mod of(ScalarExpression dividend, Number divisor) {
        return new Mod(dividend, Literal.of(divisor));
    }

    public static Mod of(Number dividend, ScalarExpression divisor) {
        return new Mod(Literal.of(dividend), divisor);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
