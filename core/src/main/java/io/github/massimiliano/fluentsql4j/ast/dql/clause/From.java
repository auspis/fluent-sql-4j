package io.github.massimiliano.fluentsql4j.ast.dql.clause;

import io.github.massimiliano.fluentsql4j.ast.core.clause.Clause;
import io.github.massimiliano.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.massimiliano.fluentsql4j.ast.dql.source.FromSource;
import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import java.util.List;
import java.util.stream.Stream;

public record From(List<FromSource> sources) implements Clause {

    public static From fromTable(String name) {
        return new From(List.of(new TableIdentifier(name)));
    }

    public static From fromTable(String name, String alias) {
        return new From(List.of(new TableIdentifier(name, alias)));
    }

    public static From of(FromSource... fromSource) {
        return new From(Stream.of(fromSource).toList());
    }

    public static From nullObject() {
        return new From(List.of());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
