package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.item.SqlItem;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ReferencesItem implements SqlItem {

    private final String table;
    private final List<String> columns;

    public ReferencesItem(String table, String... columns) {
        this(table, Stream.of(columns).toList());
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
