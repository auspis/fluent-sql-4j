package lan.tlab.sqlbuilder.ast.statement;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CreateTableStatement implements Statement {

    private final TableDefinition tableDefinition;

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
