package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface CurrentDateRenderStrategy extends ExpressionRenderStrategy {

    public String render(CurrentDate functionCall, SqlRenderer sqlRenderer);

    public static CurrentDateRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer) -> "CURRENT_DATE()";
    }

    public static CurrentDateRenderStrategy mysql() {
        return (functionCall, sqlRenderer) -> "CURDATE()";
    }
}
