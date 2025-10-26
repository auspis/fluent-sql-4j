package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.number.Power;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public class PowerRenderStrategy implements ExpressionRenderStrategy {

    public String render(Power functionCall, SqlRenderer sqlRenderer, AstContext ctx) {
        return String.format(
                "POWER(%s, %s)",
                functionCall.base().accept(sqlRenderer, ctx),
                functionCall.exponent().accept(sqlRenderer, ctx));
    }
}
