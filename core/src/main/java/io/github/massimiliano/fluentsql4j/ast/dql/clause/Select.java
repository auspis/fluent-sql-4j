package io.github.massimiliano.fluentsql4j.ast.dql.clause;

import io.github.massimiliano.fluentsql4j.ast.core.clause.Clause;
import io.github.massimiliano.fluentsql4j.ast.dql.projection.Projection;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

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
