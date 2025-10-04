package lan.tlab.r4j.sql.ast.clause.selection.projection;

import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.expression.item.As;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lombok.Getter;

@Getter
public abstract class Projection implements Visitable {

    protected final Expression expression;
    protected final As as;

    protected Projection(Expression expression) {
        this(expression, As.nullObject());
    }

    protected Projection(Expression expression, String as) {
        this(expression, new As(as));
    }

    protected Projection(Expression expression, As as) {
        this.expression = expression;
        this.as = as;
    }
}
