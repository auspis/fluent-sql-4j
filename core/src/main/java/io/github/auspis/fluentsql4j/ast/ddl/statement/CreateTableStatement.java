package io.github.auspis.fluentsql4j.ast.ddl.statement;

import io.github.auspis.fluentsql4j.ast.ddl.definition.TableDefinition;
import io.github.auspis.fluentsql4j.ast.visitor.AstContext;
import io.github.auspis.fluentsql4j.ast.visitor.Visitor;

public record CreateTableStatement(TableDefinition tableDefinition) implements DataDefinitionStatement {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
