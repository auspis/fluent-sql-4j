package lan.tlab.sqlbuilder.ast.expression.bool;

import lan.tlab.sqlbuilder.ast.expression.scalar.ScalarExpression;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Like implements BooleanExpression {

    private final ScalarExpression expression;
    private final String pattern;

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
