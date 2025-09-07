package lan.tlab.sqlbuilder.ast.clause.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.clause.Clause;
import lan.tlab.sqlbuilder.ast.clause.selection.projection.Projection;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
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
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
