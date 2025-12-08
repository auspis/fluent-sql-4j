package lan.tlab.r4j.jdsql.ast.core.predicate;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.core.expression.Expression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record In(Expression expression, List<Expression> values) implements Predicate {

    public In(Expression expression, Expression... values) {
        this(expression, Stream.of(values).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
