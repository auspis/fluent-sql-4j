package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression;

import lan.tlab.r4j.sql.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;

public interface CurrentDateRenderStrategy extends ExpressionRenderStrategy {

    public String render(CurrentDate functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static CurrentDateRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> "CURRENT_DATE()";
    }

    public static CurrentDateRenderStrategy mysql() {
        return (functionCall, sqlRenderer, ctx) -> "CURDATE()";
    }
}
