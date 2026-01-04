package io.github.massimiliano.fluentsql4j.ast.dql.projection;

import io.github.massimiliano.fluentsql4j.ast.core.expression.Expression;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.Alias;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;

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
