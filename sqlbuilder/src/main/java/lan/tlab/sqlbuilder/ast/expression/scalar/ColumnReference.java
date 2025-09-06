package lan.tlab.sqlbuilder.ast.expression.scalar;

import lan.tlab.sqlbuilder.ast.expression.item.ddl.TableDefinition;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ColumnReference implements ScalarExpression {

    private final String table;
    private final String column;

    public static ColumnReference of(TableDefinition table, String name) {
        return of(table.getName(), table.getColumnByBusinessName(name).getName());
    }

    public static ColumnReference of(String table, String name) {
        return builder().table(table).column(name).build();
    }

    public static ColumnReference star() {
        return of("", "*");
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
