package io.github.auspis.fluentsql4j.ast.dql.clause;

import io.github.auspis.fluentsql4j.ast.core.clause.Clause;
import io.github.auspis.fluentsql4j.ast.core.predicate.NullPredicate;
import io.github.auspis.fluentsql4j.ast.core.predicate.Predicate;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

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
