package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.common.predicate.In;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.InRenderStrategy;

public class StandardSqlInRenderStrategy implements InRenderStrategy {

    @Override
    public String render(In expression, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "%s IN(%s)",
                expression.expression().accept(sqlRenderer, ctx),
                expression.values().stream()
                        .map(e -> e.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(", ")));
    }
}
