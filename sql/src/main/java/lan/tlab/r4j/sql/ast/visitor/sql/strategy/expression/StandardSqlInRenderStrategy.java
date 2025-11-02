package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.predicate.In;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class StandardSqlInRenderStrategy implements ExpressionRenderStrategy {

    public String render(In expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s IN(%s)",
                expression.expression().accept(sqlRenderer, ctx),
                expression.values().stream()
                        .map(e -> e.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", ")));
    }
}
