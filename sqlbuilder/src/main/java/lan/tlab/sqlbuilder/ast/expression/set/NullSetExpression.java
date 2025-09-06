package lan.tlab.sqlbuilder.ast.expression.set;

import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;

public class NullSetExpression implements SetExpression {

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
