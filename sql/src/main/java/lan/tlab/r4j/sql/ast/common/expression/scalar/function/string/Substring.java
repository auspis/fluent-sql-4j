package lan.tlab.r4j.sql.ast.common.expression.scalar.function.string;

import lan.tlab.r4j.sql.ast.common.expression.scalar.Literal;
import lan.tlab.r4j.sql.ast.common.expression.scalar.NullScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Substring(ScalarExpression expression, ScalarExpression startPosition, ScalarExpression length)
        implements FunctionCall {

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
