package lan.tlab.sqlbuilder.ast.clause.orderby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.clause.Clause;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@Getter
public class OrderBy implements Clause {

    @Default
    private final List<Sorting> sortings = new ArrayList<>();

    public static OrderBy of(Sorting... sortings) {
        return builder().sortings(Stream.of(sortings).toList()).build();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
