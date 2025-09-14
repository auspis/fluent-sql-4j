package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number;

import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Round implements FunctionCall {
    private final ScalarExpression numericExpression;
    private final ScalarExpression decimalPlaces;

    public static Round of(Number value) {
        return of(Literal.of(value), new NullScalarExpression());
    }

    public static Round of(Number value, Number decimalPlaces) {
        return of(Literal.of(value), Literal.of(decimalPlaces));
    }

    public static Round of(ScalarExpression numericExpression) {
        return of(numericExpression, new NullScalarExpression());
    }

    public static Round of(ScalarExpression numericExpression, int number) {
        return of(numericExpression, Literal.of(number));
    }

    public static Round of(ScalarExpression numericExpression, ScalarExpression decimalPlaces) {
        return new Round(numericExpression, decimalPlaces);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this);
    }
}
