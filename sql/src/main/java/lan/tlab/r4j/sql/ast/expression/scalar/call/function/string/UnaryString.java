package lan.tlab.r4j.sql.ast.expression.scalar.call.function.string;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record UnaryString(String functionName, ScalarExpression expression) implements FunctionCall {
    // TODO: evaluate if the record is the correct solution: the creation should go only through factory methods. see
    // DateArithmetic.java

    // private UnaryString(String functionName, ScalarExpression expression) {
    //     this.functionName = functionName;
    //     this.expression = expression;
    // }

    public static UnaryString lower(ScalarExpression stringExpression) {
        return new UnaryString("LOWER", stringExpression);
    }

    public static UnaryString upper(ScalarExpression stringExpression) {
        return new UnaryString("UPPER", stringExpression);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
