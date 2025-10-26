package lan.tlab.r4j.sql.ast.clause.orderby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.clause.Clause;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record OrderBy(List<Sorting> sortings) implements Clause {

    public OrderBy {
        if (sortings == null) {
            sortings = new ArrayList<>();
        }
    }

    public static OrderBy of(Sorting... sortings) {
        return new OrderBy(Stream.of(sortings).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
