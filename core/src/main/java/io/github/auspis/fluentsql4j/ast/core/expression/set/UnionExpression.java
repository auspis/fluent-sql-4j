package io.github.auspis.fluentsql4j.ast.core.expression.set;

import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record UnionExpression(SetExpression left, SetExpression right, UnionType type) implements SetExpression {

    public enum UnionType {
        UNION_DISTINCT,
        UNION_ALL
    }

    public static UnionExpression union(SetExpression left, SetExpression right) {
        return new UnionExpression(left, right, UnionType.UNION_DISTINCT);
    }

    public static UnionExpression unionAll(SetExpression left, SetExpression right) {
        return new UnionExpression(left, right, UnionType.UNION_ALL);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
