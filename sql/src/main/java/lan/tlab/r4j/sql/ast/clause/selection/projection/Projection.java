package lan.tlab.r4j.sql.ast.clause.selection.projection;

import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lombok.Getter;

@Getter
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
}
