package lan.tlab.r4j.jdsql.ast.dql.projection;

import lan.tlab.r4j.jdsql.ast.core.expression.Expression;
import lan.tlab.r4j.jdsql.ast.core.identifier.Alias;
import lan.tlab.r4j.jdsql.ast.visitor.Visitable;

public abstract class Projection implements Visitable {

    protected final Expression expression;
    protected final Alias as;

    protected Projection(Expression expression) {
        this(expression, Alias.nullObject());
    }

    protected Projection(Expression expression, String as) {
        this(expression, new Alias(as));
    }

    protected Projection(Expression expression, Alias as) {
        this.expression = expression;
        this.as = as;
    }

    public Alias as() {
        return as;
    }

    public Expression expression() {
        return expression;
    }
}
