package lan.tlab.r4j.sql.ast.clause.selection.projection;

import lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate.AggregateCall;
import lan.tlab.r4j.sql.ast.identifier.Alias;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public class AggregateCallProjection extends Projection {

    public AggregateCallProjection(AggregateCall expression) {
        super(expression);
    }

    public AggregateCallProjection(AggregateCall expression, String as) {
        super(expression, as);
    }

    public AggregateCallProjection(AggregateCall expression, Alias as) {
        super(expression, as);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
