package lan.tlab.sqlbuilder.ast.expression.bool;

import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;

public class NullBooleanExpression implements BooleanExpression {

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
