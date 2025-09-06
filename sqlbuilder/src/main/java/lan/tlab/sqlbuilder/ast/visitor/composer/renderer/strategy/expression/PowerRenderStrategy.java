package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Power;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class PowerRenderStrategy implements ExpressionRenderStrategy {

    public String render(Power functionCall, SqlRenderer sqlRenderer) {
        return String.format(
                "POWER(%s, %s)",
                functionCall.getBase().accept(sqlRenderer),
                functionCall.getExponent().accept(sqlRenderer));
    }
}
