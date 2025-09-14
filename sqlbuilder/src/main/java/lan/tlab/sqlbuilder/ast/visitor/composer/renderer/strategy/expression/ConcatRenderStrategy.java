package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import java.util.stream.Collectors;
import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.Concat;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.dialect.mysql.strategy.expression.MySqlConcatRenderStrategy;

public interface ConcatRenderStrategy extends ExpressionRenderStrategy {

    public String render(Concat functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static ConcatRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "(%s)",
                functionCall.getStringExpressions().stream()
                        .map(e -> e.accept(sqlRenderer, ctx))
                        .collect(Collectors.joining(" || ")));
    }

    public static ConcatRenderStrategy mysql() {
        return new MySqlConcatRenderStrategy();
    }
}
