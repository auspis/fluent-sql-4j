package lan.tlab.r4j.sql.ast.clause.selection.projection;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public class ScalarExpressionProjection extends Projection {

    public ScalarExpressionProjection(ScalarExpression expression) {
        super(expression);
    }

    public ScalarExpressionProjection(ScalarExpression expression, String as) {
        super(expression, as);
    }

    public ScalarExpressionProjection(ScalarExpression expression, Alias as) {
        super(expression, as);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
