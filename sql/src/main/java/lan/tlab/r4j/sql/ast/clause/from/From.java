package lan.tlab.r4j.sql.ast.clause.from;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.clause.Clause;
import lan.tlab.r4j.sql.ast.clause.from.source.FromSource;
import lan.tlab.r4j.sql.ast.expression.item.Table;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

@Builder
@Getter
public class From implements Clause {

    @Singular
    private final List<FromSource> sources;

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
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
