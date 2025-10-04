package lan.tlab.r4j.sql.ast.statement.ddl.definition;

import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitable;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Index implements Visitable {

    private final String name;
    private final List<String> columnNames;

    public Index(String name, String... columns) {
        this(name, Stream.of(columns).toList());
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
