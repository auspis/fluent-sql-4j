package lan.tlab.r4j.sql.ast.clause.fetch;

import java.util.Objects;
import lan.tlab.r4j.sql.ast.clause.Clause;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Fetch(Integer offset, Integer rows) implements Clause {

    public Fetch {
        if (offset == null) {
            offset = 0;
        }
    }

    public static Fetch of(Integer rows) {
        return new Fetch(0, rows);
    }

    public static Fetch of(Integer offset, Integer rows) {
        return new Fetch(offset, rows);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public boolean isActive() {
        return !Objects.isNull(rows);
    }
}
