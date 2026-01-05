package io.github.auspis.fluentsql4j.ast.dql.clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import io.github.auspis.fluentsql4j.ast.core.clause.Clause;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record OrderBy(List<Sorting> sortings) implements Clause {

    public OrderBy {
        if (sortings == null) {
            sortings = Collections.unmodifiableList(new ArrayList<>());
        }
    }

    public static OrderBy nullObject() {
        return new OrderBy(null);
    }

    public static OrderBy of(Sorting... sortings) {
        return new OrderBy(Stream.of(sortings).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
