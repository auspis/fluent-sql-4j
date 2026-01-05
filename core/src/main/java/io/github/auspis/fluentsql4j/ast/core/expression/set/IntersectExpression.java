package io.github.auspis.fluentsql4j.ast.core.expression.set;

import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record IntersectExpression(SetExpression leftSetExpression, SetExpression rightSetExpression, IntersectType type)
        implements SetExpression {

    public enum IntersectType {
        INTERSECT_DISTINCT,
        INTERSECT_ALL
    }

    public static IntersectExpression intersect(SetExpression left, SetExpression right) {
        return new IntersectExpression(left, right, IntersectType.INTERSECT_DISTINCT);
    }

    public static IntersectExpression intersectAll(SetExpression left, SetExpression right) {
        return new IntersectExpression(left, right, IntersectType.INTERSECT_ALL);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
