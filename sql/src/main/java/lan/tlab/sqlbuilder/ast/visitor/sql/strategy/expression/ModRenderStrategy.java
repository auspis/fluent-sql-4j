package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public class ModRenderStrategy implements ExpressionRenderStrategy {

    public String render(Mod functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "MOD(%s, %s)",
                functionCall.getDividend().accept(sqlRenderer, ctx),
                functionCall.getDivisor().accept(sqlRenderer, ctx));
    }
}
