package lan.tlab.r4j.sql.ast.expression.scalar.call.function.number;

import lan.tlab.r4j.sql.ast.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record UnaryNumeric(UnaryNumericFunctionName functionName, ScalarExpression numericExpression)
        implements FunctionCall {

    public enum UnaryNumericFunctionName {
        ABS,
        CEIL,
        FLOOR,
        SQRT
    }

    public static UnaryNumeric abs(Number value) {
        return abs(Literal.of(value));
    }

    public static UnaryNumeric abs(ScalarExpression numericExpression) {
        return new UnaryNumeric(UnaryNumericFunctionName.ABS, numericExpression);
    }

    public static UnaryNumeric ceil(Number value) {
        return ceil(Literal.of(value));
    }

    public static UnaryNumeric ceil(ScalarExpression numericExpression) {
        return new UnaryNumeric(UnaryNumericFunctionName.CEIL, numericExpression);
    }

    public static UnaryNumeric floor(Number value) {
        return floor(Literal.of(value));
    }

    public static UnaryNumeric floor(ScalarExpression numericExpression) {
        return new UnaryNumeric(UnaryNumericFunctionName.FLOOR, numericExpression);
    }

    public static UnaryNumeric sqrt(Number value) {
        return sqrt(Literal.of(value));
    }

    public static UnaryNumeric sqrt(ScalarExpression numericExpression) {
        return new UnaryNumeric(UnaryNumericFunctionName.SQRT, numericExpression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
