package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.string.Replace;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class ReplaceRenderStrategy implements ExpressionRenderStrategy {

    public String render(Replace functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "REPLACE(%s, %s, %s)",
                functionCall.expression().accept(sqlRenderer, ctx),
                functionCall.oldSubstring().accept(sqlRenderer, ctx),
                functionCall.newSubstring().accept(sqlRenderer, ctx));
    }
}
