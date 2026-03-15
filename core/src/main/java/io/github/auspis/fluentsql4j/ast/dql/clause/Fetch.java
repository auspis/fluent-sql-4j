package io.github.auspis.fluentsql4j.ast.dql.clause;

import io.github.auspis.fluentsql4j.ast.core.clause.Clause;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;
import java.util.Objects;

public record Fetch(Long offset, Long rows) implements Clause {

    public Fetch {
        if (offset == null) {
            offset = 0L;
        }
    }

    public static Fetch nullObject() {
        return new Fetch(0L, null);
    }

    public static Fetch of(Long rows) {
        return new Fetch(0L, rows);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public boolean isActive() {
        return !Objects.isNull(rows);
    }
}
