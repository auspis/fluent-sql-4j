package lan.tlab.r4j.jdsql.ast.dql.clause;

import lan.tlab.r4j.jdsql.ast.core.clause.Clause;
import lan.tlab.r4j.jdsql.ast.core.predicate.AndOr;
import lan.tlab.r4j.jdsql.ast.core.predicate.NullPredicate;
import lan.tlab.r4j.jdsql.ast.core.predicate.Predicate;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record Where(Predicate condition) implements Clause {

    public Where {
        if (condition == null) {
            condition = new NullPredicate();
        }
    }

    public static Where nullObject() {
        return new Where(new NullPredicate());
    }

    public static Where andOf(Predicate... conditions) {
        return new Where(AndOr.and(conditions));
    }

    public static Where of(Predicate condition) {
        return new Where(condition);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
