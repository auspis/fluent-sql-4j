package lan.tlab.r4j.sql.ast.dql.clause;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.common.clause.Clause;
import lan.tlab.r4j.sql.ast.common.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.dql.source.FromSource;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

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
