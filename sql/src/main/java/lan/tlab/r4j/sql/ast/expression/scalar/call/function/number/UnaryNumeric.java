package lan.tlab.r4j.sql.ast.expression.scalar.call.function.number;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class UnaryNumeric implements FunctionCall {
    private final String functionName;
    private final ScalarExpression numericExpression;

    public static UnaryNumeric abs(Number value) {
        return abs(Literal.of(value));
    }

    public static UnaryNumeric abs(ScalarExpression numericExpression) {
        return new UnaryNumeric("ABS", numericExpression);
    }

    public static UnaryNumeric ceil(Number value) {
        return ceil(Literal.of(value));
    }

    public static UnaryNumeric ceil(ScalarExpression numericExpression) {
        return new UnaryNumeric("CEIL", numericExpression);
    }

    public static UnaryNumeric floor(Number value) {
        return floor(Literal.of(value));
    }

    public static UnaryNumeric floor(ScalarExpression numericExpression) {
        return new UnaryNumeric("FLOOR", numericExpression);
    }

    public static UnaryNumeric sqrt(Number value) {
        return sqrt(Literal.of(value));
    }

    public static UnaryNumeric sqrt(ScalarExpression numericExpression) {
        return new UnaryNumeric("SQRT", numericExpression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
