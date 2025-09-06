package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Mod;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class ModRenderStrategy implements ExpressionRenderStrategy {

    public String render(Mod functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "MOD(%s, %s)",
                functionCall.getDividend().accept(sqlRenderer),
                functionCall.getDivisor().accept(sqlRenderer));
    }
}
