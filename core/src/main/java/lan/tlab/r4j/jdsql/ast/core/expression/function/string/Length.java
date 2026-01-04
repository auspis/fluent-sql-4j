package lan.tlab.r4j.jdsql.ast.core.expression.function.string;

import lan.tlab.r4j.jdsql.ast.core.expression.function.FunctionCall;
import lan.tlab.r4j.jdsql.ast.core.expression.scalar.ScalarExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Length(ScalarExpression expression) implements FunctionCall {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
