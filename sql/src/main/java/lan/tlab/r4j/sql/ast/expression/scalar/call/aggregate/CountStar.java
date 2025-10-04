package lan.tlab.r4j.sql.ast.expression.scalar.call.aggregate;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Getter;

@Getter
public class CountStar implements AggregateCall {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
