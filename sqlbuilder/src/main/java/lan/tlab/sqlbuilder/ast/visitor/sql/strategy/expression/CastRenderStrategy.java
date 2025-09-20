package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public interface CastRenderStrategy extends ExpressionRenderStrategy {

    String render(Cast functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static CastRenderStrategy standard() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "CAST(%s AS %s)", functionCall.getExpression().accept(sqlRenderer, ctx), functionCall.getDataType());
    }

    public static CastRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) -> String.format(
                "CONVERT(%s, %s)",
                functionCall.getDataType(), functionCall.getExpression().accept(sqlRenderer, ctx));
    }
}
