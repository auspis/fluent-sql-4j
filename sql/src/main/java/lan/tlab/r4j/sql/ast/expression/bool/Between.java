package lan.tlab.r4j.sql.ast.expression.bool;

import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Between implements BooleanExpression {

    private final Expression testExpression;
    private final Expression startExpression;
    private final Expression endExpression;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
