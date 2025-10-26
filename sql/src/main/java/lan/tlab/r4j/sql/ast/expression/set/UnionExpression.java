package lan.tlab.r4j.sql.ast.expression.set;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record UnionExpression(SetExpression left, SetExpression right, UnionType type) implements SetExpression {

    public enum UnionType {
        UNION_DISTINCT,
        UNION_ALL
    }

    public static UnionExpression union(SetExpression left, TableExpression right) {
        return new UnionExpression(left, right, UnionType.UNION_DISTINCT);
    }

    public static UnionExpression unionAll(SetExpression left, TableExpression right) {
        return new UnionExpression(left, right, UnionType.UNION_ALL);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
