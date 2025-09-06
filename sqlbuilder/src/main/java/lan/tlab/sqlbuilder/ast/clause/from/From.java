package lan.tlab.sqlbuilder.ast.clause.from;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.sqlbuilder.ast.clause.Clause;
import lan.tlab.sqlbuilder.ast.clause.from.source.FromSource;
import lan.tlab.sqlbuilder.ast.expression.item.Table;
import lan.tlab.sqlbuilder.ast.visitor.SqlVisitor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class From implements Clause {

    @Singular
    private final List<FromSource> sources;

    public static From fromTable(Table table) {
        return fromTable(table.getName());
    }

    public static From fromTable(String name) {
        return From.builder().source(new Table(name)).build();
    }

    public static From fromTable(String name, String alias) {
        return From.builder().source(new Table(name, alias)).build();
    }

    public static From of(FromSource... fromSource) {
        return From.builder().sources(Stream.of(fromSource).toList()).build();
    }

    @Override
    public <T> T accept(SqlVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
