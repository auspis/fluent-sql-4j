package io.github.auspis.fluentsql4j.ast.ddl.definition;

import java.util.List;
import java.util.stream.Stream;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitable;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record ReferencesItem(String table, List<String> columns) implements Visitable {

    public ReferencesItem(String table, String... columns) {
        this(table, Stream.of(columns).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
