package lan.tlab.sqlbuilder.ast.clause.selection.projection;

import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;

public class ScalarExpressionProjection extends Projection {

    public ScalarExpressionProjection(ScalarExpression expression) {
        super(expression);
    }

    public ScalarExpressionProjection(ScalarExpression expression, String as) {
        super(expression, as);
    }

    public ScalarExpressionProjection(ScalarExpression expression, As as) {
        super(expression, as);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
