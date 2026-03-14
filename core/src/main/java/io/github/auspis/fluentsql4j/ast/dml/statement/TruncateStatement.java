package io.github.auspis.fluentsql4j.ast.dml.statement;

import io.github.auspis.fluentsql4j.ast.core.identifier.TableIdentifier;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record TruncateStatement(TableIdentifier table) implements DataManipulationStatement {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static TruncateStatementBuilder builder() {
        return new TruncateStatementBuilder();
    }

    public static class TruncateStatementBuilder {
        private TableIdentifier table;

        public TruncateStatementBuilder table(TableIdentifier table) {
            this.table = table;
            return this;
        }

        public TruncateStatement build() {
            return new TruncateStatement(table);
        }
    }
}
