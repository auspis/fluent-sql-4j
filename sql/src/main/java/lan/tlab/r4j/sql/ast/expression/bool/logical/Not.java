package lan.tlab.r4j.sql.ast.expression.bool.logical;

import lan.tlab.r4j.sql.ast.expression.bool.BooleanExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Not implements LogicalExpression {

    private final BooleanExpression expression;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
