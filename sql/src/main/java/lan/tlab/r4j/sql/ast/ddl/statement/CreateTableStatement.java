package lan.tlab.r4j.sql.ast.ddl.statement;

import lan.tlab.r4j.sql.ast.ddl.definition.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record CreateTableStatement(TableDefinition tableDefinition) implements DataDefinitionStatement {

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
