package lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
@Getter
public class CountDistinct implements AggregateCall {

    private final ScalarExpression expression;

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return null;
    }
}
