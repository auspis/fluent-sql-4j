package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.number.Power;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public class PowerRenderStrategy implements ExpressionRenderStrategy {

    public String render(Power functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "POWER(%s, %s)",
                functionCall.getBase().accept(sqlRenderer, ctx),
                functionCall.getExponent().accept(sqlRenderer, ctx));
    }
}
