package lan.tlab.r4j.jdsql.plugin.builtin.sql2016.ast.visitor.sql.strategy.expression.window;

import lan.tlab.r4j.jdsql.ast.common.expression.scalar.window.RowNumber;
import lan.tlab.r4j.jdsql.ast.visitor.AstContext;
import lan.tlab.r4j.jdsql.ast.visitor.sql.SqlRenderer;
import lan.tlab.r4j.jdsql.ast.visitor.sql.strategy.expression.window.RowNumberRenderStrategy;

public class StandardSqlRowNumberRenderStrategy implements RowNumberRenderStrategy {
    @Override
    public String render(RowNumber rowNumber, SqlRenderer sqlRenderer, AstContext ctx) {
        StringBuilder sql = new StringBuilder("ROW_NUMBER()");

        if (rowNumber.overClause() != null) {
            sql.append(" ").append(rowNumber.overClause().accept(sqlRenderer, ctx));
        }

        return sql.toString();
    }
}
