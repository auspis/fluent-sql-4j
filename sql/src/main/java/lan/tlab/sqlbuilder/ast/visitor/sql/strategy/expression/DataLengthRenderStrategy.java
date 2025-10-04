package lan.tlab.sqlbuilder.ast.visitor.sql.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.sqlbuilder.ast.visitor.AstContext;
import lan.tlab.sqlbuilder.ast.visitor.sql.SqlRenderer;

public interface DataLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(DataLength functionCall, SqlRenderer sqlRenderer, AstContext ctx);

    public static DataLengthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer, ctx) ->
                String.format("DATALENGTH(%s)", functionCall.getExpression().accept(sqlRenderer, ctx));
    }

    public static DataLengthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer, ctx) -> {
            throw new UnsupportedOperationException("The standard SQL 2008 does not support DATALENGTH funcion call");
        };
    }

    public static DataLengthRenderStrategy mysql() {
        return (functionCall, sqlRenderer, ctx) -> {
            throw new UnsupportedOperationException("The MySQL does not support DATALENGTH funcion call");
        };
    }
}
