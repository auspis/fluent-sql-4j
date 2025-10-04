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
public class UnaryString implements FunctionCall {

    private final String functionName;
    private final ScalarExpression expression;

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
