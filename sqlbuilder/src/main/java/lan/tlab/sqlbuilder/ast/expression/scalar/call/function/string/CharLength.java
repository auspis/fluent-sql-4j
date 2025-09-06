package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CharLength implements FunctionCall {

    private final ScalarExpression expression;

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
