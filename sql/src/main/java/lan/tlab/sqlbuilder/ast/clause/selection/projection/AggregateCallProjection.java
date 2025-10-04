package lan.tlab.sqlbuilder.ast.clause.selection.projection;

import lan.tlab.sqlbuilder.ast.expression.item.As;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;

public class AggregateCallProjection extends Projection {

    public AggregateCallProjection(AggregateCall expression) {
        super(expression);
    }

    public AggregateCallProjection(AggregateCall expression, String as) {
        super(expression, as);
    }

    public AggregateCallProjection(AggregateCall expression, As as) {
        super(expression, as);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
