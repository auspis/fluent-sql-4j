package io.github.auspis.fluentsql4j.ast.core.expression.scalar;

import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record ColumnReference(String table, String column) implements ScalarExpression {

    public static ColumnReference of(String table, String name) {
        return new ColumnReference(table, name);
    }

    public static ColumnReference star() {
        return new ColumnReference("", "*");
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
