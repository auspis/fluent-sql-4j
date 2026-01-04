package io.github.massimiliano.fluentsql4j.ast.ddl.definition;

import io.github.massimiliano.fluentsql4j.ast.visitor.AstContext;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitable;
import io.github.massimiliano.fluentsql4j.ast.visitor.Visitor;
import java.util.List;
import java.util.stream.Stream;

public record IndexDefinition(String name, List<String> columnNames) implements Visitable {

    public IndexDefinition(String name, String... columns) {
        this(name, Stream.of(columns).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
