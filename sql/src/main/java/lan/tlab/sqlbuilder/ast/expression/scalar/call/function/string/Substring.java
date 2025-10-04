package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string;

import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.NullScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Substring implements FunctionCall {

    private final ScalarExpression expression;
    private final ScalarExpression startPosition;
    private final ScalarExpression length;

    public static Substring of(ScalarExpression expression, int startPosition) {
        return of(expression, Literal.of(startPosition));
    }

    public static Substring of(ScalarExpression expression, int startPosition, int length) {
        return of(expression, Literal.of(startPosition), Literal.of(length));
    }

    public static Substring of(ScalarExpression expression, ScalarExpression startPosition) {
        return of(expression, startPosition, new NullScalarExpression());
    }

    public static Substring of(ScalarExpression expression, ScalarExpression startPosition, ScalarExpression length) {
        return new Substring(expression, startPosition, length);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
