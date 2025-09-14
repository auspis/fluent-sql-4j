package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.Getter;

@Getter
public class CurrentDateTime implements FunctionCall {

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this);
    }
}
