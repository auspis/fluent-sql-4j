package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.datetime.CurrentDateTime;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public interface CurrentDateTimeRenderStrategy extends ExpressionRenderStrategy {

    public String render(CurrentDateTime functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static CurrentDateTimeRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> "CURRENT_TIMESTAMP()";
    }

    public static CurrentDateTimeRenderStrategy mysql() {
        return (functionCall, sqlRenderer, ctx) -> "NOW()";
    }

    public static CurrentDateTimeRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) -> "GETDATE()";
    }

    public static CurrentDateTimeRenderStrategy oracle() {
        return (functionCall, sqlRenderer, ctx) -> "SYSDATE()";
    }
}
