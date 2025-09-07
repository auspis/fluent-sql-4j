package lan.tlab.sqlbuilder.ast.expression.item.ddl;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.expression.item.SqlItem;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Index implements SqlItem {

    private final String name;
    private final List<String> columnNames;

    public Index(String name, String... columns) {
        this(name, Stream.of(columns).toList());
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
