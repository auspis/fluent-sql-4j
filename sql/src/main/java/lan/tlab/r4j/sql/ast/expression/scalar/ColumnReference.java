package lan.tlab.r4j.sql.ast.expression.scalar;

import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ColumnReference implements ScalarExpression {

    @Default
    private final String table = "";

    @Default
    private final String column = "";

    public static ColumnReference of(String table, String name) {
        return builder().table(table).column(name).build();
    }

    public static ColumnReference star() {
        return of("", "*");
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
