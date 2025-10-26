package lan.tlab.r4j.sql.ast.predicate;

import lan.tlab.r4j.sql.ast.expression.scalar.ScalarExpression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Like(ScalarExpression expression, String pattern) implements Predicate {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
