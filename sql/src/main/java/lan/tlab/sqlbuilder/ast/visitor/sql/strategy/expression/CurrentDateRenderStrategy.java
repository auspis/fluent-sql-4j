package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDate;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public interface CurrentDateRenderStrategy extends ExpressionRenderStrategy {

    public String render(CurrentDate functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static CurrentDateRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> "CURRENT_DATE()";
    }

    public static CurrentDateRenderStrategy mysql() {
        return (functionCall, sqlRenderer, ctx) -> "CURDATE()";
    }
}
