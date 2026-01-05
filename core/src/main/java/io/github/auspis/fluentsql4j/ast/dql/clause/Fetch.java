package io.github.auspis.fluentsql4j.ast.dql.clause;

import java.util.Objects;
import io.github.auspis.fluentsql4j.ast.core.clause.Clause;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record Fetch(Integer offset, Integer rows) implements Clause {

    public Fetch {
        if (offset == null) {
            offset = 0;
        }
    }

    public static Fetch nullObject() {
        return new Fetch(0, null);
    }

    public static Fetch of(Integer rows) {
        return new Fetch(0, rows);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public boolean isActive() {
        return !Objects.isNull(rows);
    }
}
