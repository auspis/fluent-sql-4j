package lan.tlab.r4j.jdsql.ast.core.expression.set;

import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record ExceptExpression(SetExpression left, SetExpression right, boolean distinct) implements SetExpression {

    public static ExceptExpression except(SetExpression left, SetExpression right) {
        return new ExceptExpression(left, right, true);
    }

    public static ExceptExpression exceptAll(SetExpression left, SetExpression right) {
        return new ExceptExpression(left, right, false);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
