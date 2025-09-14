package lan.tlab.sqlbuilder.ast.expression.bool;

import lan.tlab.sqlbuilder.ast.expression.Expression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Between implements BooleanExpression {

    private final Expression testExpression;
    private final Expression startExpression;
    private final Expression endExpression;

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
