package lan.tlab.r4j.sql.ast.predicate;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record In(Expression expression, List<Expression> values) implements Predicate {

    public In(Expression expression, Expression... values) {
        this(expression, Stream.of(values).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
