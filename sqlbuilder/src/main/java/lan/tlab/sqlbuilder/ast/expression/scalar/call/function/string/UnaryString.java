package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
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
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
