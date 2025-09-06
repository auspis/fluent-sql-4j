package lan.tlab.sqlbuilder.ast.visitor.composer.renderer.strategy.expression;

import lan.tlab.sqlbuilder.ast.expression.scalar.call.function.string.DataLength;
import lan.tlab.sqlbuilder.ast.visitor.composer.renderer.SqlRenderer;

public interface DataLengthRenderStrategy extends ExpressionRenderStrategy {

    public String render(DataLength functionCall, SqlRenderer sqlRenderer);

    public static DataLengthRenderStrategy sqlServer() {
        return (functionCall, sqlRenderer) ->
                String.format("DATALENGTH(%s)", functionCall.getExpression().accept(sqlRenderer));
    }

    public static DataLengthRenderStrategy standardSql2008() {
        return (functionCall, sqlRenderer) -> {
            throw new UnsupportedOperationException("The standard SQL 2008 does not support DATALENGTH funcion call");
        };
    }

    public static DataLengthRenderStrategy mysql() {
        return (functionCall, sqlRenderer) -> {
            throw new UnsupportedOperationException("The MySQL does not support DATALENGTH funcion call");
        };
    }
}
