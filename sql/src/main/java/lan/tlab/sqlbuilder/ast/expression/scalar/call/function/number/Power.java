package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number;

import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Power implements FunctionCall {
    private final ScalarExpression base;
    private final ScalarExpression exponent;

    public static Power of(Number base, Number exponent) {
        return of(Literal.of(base), Literal.of(exponent));
    }

    public static Power of(ScalarExpression base, Number exponent) {
        return of(base, Literal.of(exponent));
    }

    public static Power of(Number base, ScalarExpression exponent) {
        return of(Literal.of(base), exponent);
    }

    public static Power of(ScalarExpression base, ScalarExpression exponent) {
        return new Power(base, exponent);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
