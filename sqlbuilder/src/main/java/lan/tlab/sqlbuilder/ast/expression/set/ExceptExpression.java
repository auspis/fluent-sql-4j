package lan.tlab.sqlbuilder.ast.expression.set;

import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
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
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
