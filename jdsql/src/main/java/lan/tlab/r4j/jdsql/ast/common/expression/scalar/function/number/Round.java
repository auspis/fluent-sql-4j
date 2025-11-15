package lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.number;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.common.expression.scalar.function.FunctionCall;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Round(ScalarExpression numericExpression, ScalarExpression decimalPlaces) implements FunctionCall {

    public static Round of(Number value) {
        return new Round(Literal.of(value), new NullScalarExpression());
    }

    public static Round of(Number value, Number decimalPlaces) {
        return new Round(Literal.of(value), Literal.of(decimalPlaces));
    }

    public static Round of(ScalarExpression numericExpression) {
        return new Round(numericExpression, new NullScalarExpression());
    }

    public static Round of(ScalarExpression numericExpression, int number) {
        return new Round(numericExpression, Literal.of(number));
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
