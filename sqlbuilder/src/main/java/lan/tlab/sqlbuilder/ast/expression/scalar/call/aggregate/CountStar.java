package lan.tlab.sqlbuilder.ast.expression.scalar.call.aggregate;

import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.Getter;

@Getter
public class CountStar implements AggregateCall {

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
