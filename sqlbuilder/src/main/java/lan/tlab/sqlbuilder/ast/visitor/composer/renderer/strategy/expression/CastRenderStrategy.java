package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.convert.Cast;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface CastRenderStrategy extends ExpressionRenderStrategy {

    String render(Cast functionCall, SqlRenderer sqlRenderer);

    public static CastRenderStrategy standard() {
        return (functionCall, sqlRenderer) -> String.format(
                "CAST(%s AS %s)", functionCall.getExpression().accept(sqlRenderer), functionCall.getDataType());
    }

    public static CastRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer) -> String.format(
                "CONVERT(%s, %s)",
                functionCall.getDataType(), functionCall.getExpression().accept(sqlRenderer));
    }
}
