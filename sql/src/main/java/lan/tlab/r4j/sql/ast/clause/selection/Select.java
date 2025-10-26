package lan.tlab.r4j.sql.ast.clause.selection;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.clause.Clause;
import lan.tlab.r4j.sql.ast.clause.selection.projection.Projection;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record Select(List<Projection> projections) implements Clause {

    public Select() {
        this(new ArrayList<>());
    }

    public static Select of(Projection... items) {
        return new Select(Stream.of(items).toList());
    }

    public static Select of(List<Projection> items) {
        return new Select(items);
    }

    public static SelectBuilder builder() {
        return new SelectBuilder();
    }

    public static class SelectBuilder {
        private List<Projection> projections = new ArrayList<>();

        public SelectBuilder projection(Projection projection) {
            this.projections.add(projection);
            return this;
        }

        public Select build() {
            return new Select(projections);
        }
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
