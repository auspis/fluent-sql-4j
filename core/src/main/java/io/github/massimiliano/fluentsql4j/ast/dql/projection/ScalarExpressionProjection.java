package io.github.massimiliano.fluentsql4j.ast.dql.projection;

import io.github.massimiliano.fluentsql4j.ast.core.expression.scalar.ScalarExpression;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.Alias;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;

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
