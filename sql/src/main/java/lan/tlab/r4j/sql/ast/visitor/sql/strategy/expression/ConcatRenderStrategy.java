package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.plugin.builtin.myqsl.ast.visitor.sql.strategy.expression.MySqlConcatRenderStrategy;

public interface ConcatRenderStrategy extends ExpressionRenderStrategy {

    public String render(Concat functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static ConcatRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "(%s)",
                functionCall.stringExpressions().stream()
                        .map(e -> e.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(" || ")));
    }

    public static ConcatRenderStrategy mysql() {
        return new MySqlConcatRenderStrategy();
    }
}
