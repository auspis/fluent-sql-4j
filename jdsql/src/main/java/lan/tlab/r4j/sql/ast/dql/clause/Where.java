package lan.tlab.r4j.sql.ast.dql.clause;

import lan.tlab.r4j.sql.ast.common.clause.Clause;
import lan.tlab.r4j.sql.ast.common.predicate.NullPredicate;
import lan.tlab.r4j.sql.ast.common.predicate.Predicate;
import lan.tlab.r4j.sql.ast.common.predicate.logical.AndOr;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

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
