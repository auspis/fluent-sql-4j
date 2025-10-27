package lan.tlab.r4j.sql.ast.expression.scalar.call.function.string;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record UnaryString(FunctionName functionName, ScalarExpression expression) implements FunctionCall {

    public enum FunctionName {
        LOWER,
        UPPER
    }

    public static UnaryString lower(ScalarExpression expression) {
        return new UnaryString(FunctionName.LOWER, expression);
    }

    public static UnaryString upper(ScalarExpression expression) {
        return new UnaryString(FunctionName.UPPER, expression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
