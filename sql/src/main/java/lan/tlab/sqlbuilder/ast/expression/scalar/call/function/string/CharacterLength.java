package lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.FunctionCall;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CharacterLength implements FunctionCall {

    private final ScalarExpression expression;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
