package lan.tlab.r4j.sql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.common.expression.scalar.function.string.Replace;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ReplaceRenderStrategy;

public class StandardSqlReplaceRenderStrategy implements ReplaceRenderStrategy {

    @Override
    public String render(Replace functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "REPLACE(%s, %s, %s)",
                functionCall.expression().accept(sqlRenderer, ctx),
                functionCall.oldSubstring().accept(sqlRenderer, ctx),
                functionCall.newSubstring().accept(sqlRenderer, ctx));
    }
}
