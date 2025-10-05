package lan.tlab.r4j.sql.ast.statement.ddl.definition;

import java.util.List;
import lan.tlab.r4j.sql.ast.expression.set.TableExpression;
import lan.tlab.r4j.sql.ast.identifier.TableIdentifier;
import lan.tlab.r4j.sql.ast.statement.ddl.definition.ConstraintDefinition.PrimaryKeyDefinition;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class TableDefinition implements Visitable {

    private final TableExpression table;

    @Singular
    private final List<ColumnDefinition> columns;

    private final PrimaryKeyDefinition primaryKey;

    @Singular
    private final List<ConstraintDefinition> constraints;

    @Singular
    private final List<IndexDefinition> indexes;

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }

    public static class TableDefinitionBuilder {
        public TableDefinitionBuilder name(String value) {
            return table(new TableIdentifier(value));
        }
    }
}
