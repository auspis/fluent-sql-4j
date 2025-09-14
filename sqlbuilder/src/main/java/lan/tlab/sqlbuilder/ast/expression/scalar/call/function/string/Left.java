package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string;

import lan.tlab.sqlbuilder.ast.expression.scalar.Literal;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class Left implements FunctionCall {

    private final ScalarExpression expression;
    private final ScalarExpression length;

    public static Left of(ScalarExpression expression, int startPosition) {
        return of(expression, Literal.of(startPosition));
    }

    public static Left of(ScalarExpression expression, ScalarExpression startPosition) {
        return new Left(expression, startPosition);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this);
    }
}
