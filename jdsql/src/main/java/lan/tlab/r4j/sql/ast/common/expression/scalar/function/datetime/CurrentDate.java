package lan.tlab.r4j.sql.ast.common.expression.scalar.function.datetime;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.FunctionCall;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public class CurrentDate implements FunctionCall {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
