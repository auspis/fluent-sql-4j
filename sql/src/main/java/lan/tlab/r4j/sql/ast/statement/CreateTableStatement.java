package lan.tlab.r4j.sql.ast.statement;

import lan.tlab.r4j.sql.ast.expression.item.ddl.TableDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateTableStatement implements Statement {

    private final TableDefinition tableDefinition;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
