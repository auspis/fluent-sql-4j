package lan.tlab.sqlbuilder.ast.clause.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.clause.Clause;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@AllArgsConstructor
@Getter
public class Select implements Clause {

    @Singular
    private final List<Projection> projections;

    public Select() {
        this(new ArrayList<>());
    }

    public static Select of(Projection... items) {
        return of(Stream.of(items).toList());
    }

    public static Select of(List<Projection> items) {
        return builder().projections(items).build();
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
