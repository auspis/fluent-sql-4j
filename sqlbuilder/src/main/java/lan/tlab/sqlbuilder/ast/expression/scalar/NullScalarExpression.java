package lan.tlab.sqlbuilder.ast.expression.scalar;

import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;

public class NullScalarExpression implements ScalarExpression {

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
