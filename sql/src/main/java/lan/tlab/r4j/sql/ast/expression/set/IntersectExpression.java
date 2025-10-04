package lan.tlab.r4j.sql.ast.expression.set;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class IntersectExpression implements SetExpression {

    private final SetExpression leftSetExpression;
    private final SetExpression rightSetExpression;
    private final IntersectType type;

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
