package io.github.auspis.fluentsql4j.ast.dql.clause;

import io.github.auspis.fluentsql4j.ast.core.clause.Clause;
import io.github.auspis.fluentsql4j.ast.core.predicate.AndOr;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

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
