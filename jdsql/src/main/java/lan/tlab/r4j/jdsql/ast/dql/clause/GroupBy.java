package lan.tlab.r4j.jdsql.ast.dql.clause;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import lan.tlab.r4j.jdsql.ast.common.clause.Clause;
import lan.tlab.r4j.jdsql.ast.common.expression.Expression;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.Visitor;

public record GroupBy(List<Expression> groupingExpressions) implements Clause {

    public GroupBy {
        if (groupingExpressions == null) {
            groupingExpressions = Collections.unmodifiableList(new ArrayList<>());
        }
    }

    public static GroupBy nullObject() {
        return new GroupBy(null);
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
