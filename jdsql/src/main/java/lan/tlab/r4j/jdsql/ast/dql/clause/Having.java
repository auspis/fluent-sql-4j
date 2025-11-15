package lan.tlab.r4j.jdsql.ast.dql.clause;

import lan.tlab.r4j.jdsql.ast.common.clause.Clause;
import lan.tlab.r4j.jdsql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.common.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Having(Predicate condition) implements Clause {

    public Having() {
        this(new NullPredicate());
    }

    public static Having of(Predicate condition) {
        return new Having(condition);
    }

    public static Having nullObject() {
        return new Having(new NullPredicate());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
