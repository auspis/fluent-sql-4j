package lan.tlab.sqlbuilder.ast.expression.bool;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.Expression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class In implements BooleanExpression {

    private final Expression expression;
    private final List<Expression> values;

    public In(Expression expression, Expression... values) {
        this(expression, Stream.of(values).toList());
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
