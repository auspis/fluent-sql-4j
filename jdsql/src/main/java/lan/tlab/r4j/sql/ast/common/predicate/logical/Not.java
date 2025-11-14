package lan.tlab.r4j.sql.ast.common.predicate.logical;

import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Not(Predicate expression) implements LogicalExpression {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
