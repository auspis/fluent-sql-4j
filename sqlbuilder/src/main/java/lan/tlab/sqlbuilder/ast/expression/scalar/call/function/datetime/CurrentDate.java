package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.Getter;

@Getter
public class CurrentDate implements FunctionCall {

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
