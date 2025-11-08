package lan.tlab.r4j.sql.ast.ddl.definition;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record ReferencesItem(String table, List<String> columns) implements Visitable {

    public ReferencesItem(String table, String... columns) {
        this(table, Stream.of(columns).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
