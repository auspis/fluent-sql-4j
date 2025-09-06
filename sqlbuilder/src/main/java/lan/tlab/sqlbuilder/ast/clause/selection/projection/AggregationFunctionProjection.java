package lan.tlab.sqlbuilder.ast.clause.selection.projection;

import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;

public class AggregationFunctionProjection extends Projection {

    public AggregationFunctionProjection(AggregateCall expression) {
        super(expression);
    }

    public AggregationFunctionProjection(AggregateCall expression, String as) {
        super(expression, as);
    }

    public AggregationFunctionProjection(AggregateCall expression, As as) {
        super(expression, as);
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
