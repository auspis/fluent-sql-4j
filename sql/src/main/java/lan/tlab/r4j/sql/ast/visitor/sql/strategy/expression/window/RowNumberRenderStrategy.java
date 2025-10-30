package lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.sql.ast.expression.scalar.call.window.RowNumber;
import lan.tlab.r4j.sql.ast.visitor.AstContext;
import lan.tlab.r4j.sql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.sql.ast.visitor.sql.strategy.expression.ExpressionRenderStrategy;

public class RowNumberRenderStrategy implements ExpressionRenderStrategy {

    public String render(RowNumber rowNumber, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("ROW_NUMBER()");

        if (rowNumber.getOverClause() != null) {
            sql.append(" ").append(rowNumber.getOverClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
