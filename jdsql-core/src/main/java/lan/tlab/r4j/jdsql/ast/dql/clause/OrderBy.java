package lan.tlab.r4j.jdsql.ast.dql.clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.common.clause.Clause;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

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
