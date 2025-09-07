package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import lan.tlab.sqlbuilder.ast.expression.item.ddl.Constraint.PrimaryKey;
import lan.tlab.sqlbuilder.ast.expression.set.TableExpression;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lan.tlab.sqlbuilder.ast.visitor.Visitable;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class TableDefinition implements Visitable {

    private final String name;

    private final TableExpression table;

    @Singular
    private final List<ColumnDefinition> columns;

    private final PrimaryKey primaryKey;
    @Singular
    private final List<Index> indexes;
    
    @Deprecated
    public ColumnDefinition getColumnByBusinessName(String value) {
        return columns.stream()
                .filter(c -> c.getBusinessName().equals(value))
                .findFirst()
                .orElse(ColumnDefinition.nullObject());
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
