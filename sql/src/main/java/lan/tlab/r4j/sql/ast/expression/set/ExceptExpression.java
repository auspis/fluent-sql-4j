package lan.tlab.r4j.sql.ast.expression.set;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ExceptExpression implements SetExpression {

    private final SetExpression left;
    private final SetExpression right;
    private final boolean distinct;

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
