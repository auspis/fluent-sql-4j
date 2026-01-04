package lan.tlab.r4j.jdsql.ast.core.predicate;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.core.expression.ValueExpression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

/**
 * Represents an IN predicate: {@code expression IN (value1, value2, ...)}.
 *
 * <p>Both the expression and all values in the list must be {@link ValueExpression
 * value-producing expressions}, as IN requires comparable values.
 */
public record In(ValueExpression expression, List<ValueExpression> values) implements Predicate {

    public In(ValueExpression expression, ValueExpression... values) {
        this(expression, Stream.of(values).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
