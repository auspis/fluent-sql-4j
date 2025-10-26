package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.convert.Cast;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CastRenderStrategy extends ExpressionRenderStrategy {

    String render(Cast functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static CastRenderStrategy standard() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "CAST(%s AS %s)", functionCall.expression().accept(sqlRenderer, ctx), functionCall.dataType());
    }

    public static CastRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "CONVERT(%s, %s)",
                functionCall.dataType(), functionCall.expression().accept(sqlRenderer, ctx));
    }
}
