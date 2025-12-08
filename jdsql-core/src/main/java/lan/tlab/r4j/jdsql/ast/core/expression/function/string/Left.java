package lan.tlab.r4j.jdsql.ast.core.expression.function.string;

import lan.tlab.r4j.jdsql.ast.core.expression.function.FunctionCall;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.Literal;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Left(ScalarExpression expression, ScalarExpression length) implements FunctionCall {

    public static Left of(ScalarExpression expression, int length) {
        return new Left(expression, Literal.of(length));
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
