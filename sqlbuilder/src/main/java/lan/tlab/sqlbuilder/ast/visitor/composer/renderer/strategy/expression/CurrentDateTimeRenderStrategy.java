package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface CurrentDateTimeRenderStrategy extends ExpressionRenderStrategy {

    public String render(CurrentDateTime functionCall, SqlRenderer sqlRenderer);

    public static CurrentDateTimeRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer) -> "CURRENT_TIMESTAMP()";
    }

    public static CurrentDateTimeRenderStrategy mysql() {
        return (functionCall, sqlRenderer) -> "NOW()";
    }

    public static CurrentDateTimeRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer) -> "GETDATE()";
    }

    public static CurrentDateTimeRenderStrategy oracle() {
        return (functionCall, sqlRenderer) -> "SYSDATE()";
    }
}
