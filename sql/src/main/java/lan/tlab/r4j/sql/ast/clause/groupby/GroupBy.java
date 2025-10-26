package lan.tlab.r4j.sql.ast.clause.groupby;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.sql.ast.clause.Clause;
import lan.tlab.r4j.sql.ast.expression.Expression;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.Visitor;

public record GroupBy(List<Expression> groupingExpressions) implements Clause {

    public GroupBy {
        if (groupingExpressions == null) {
            groupingExpressions = new ArrayList<>();
        }
    }

    public static GroupBy of(Expression... expressions) {
        return of(Stream.of(expressions).toList());
    }

    public static GroupBy of(List<Expression> expressions) {
        return new GroupBy(expressions);
    }

    @Override
    public <T> T accept(Visitor<T> visitor, AstContext ctx) {
        return visitor.visit(this, ctx);
    }
}
