package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Concat;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ConcatRenderStrategy;

public class StandardSqlConcatRenderStrategy implements ConcatRenderStrategy {

    @Override
    public String render(Concat functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "(%s)",
                functionCall.stringExpressions().stream()
                        .map(e -> e.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(" || ")));
    }
}
